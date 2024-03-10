package com.hayden.tracing_agent.config;

import com.hayden.tracing_agent.messaging.support.TracingBroadcasterHandle;
import com.hayden.utilitymodule.MapFunctions;
import com.hayden.utilitymodule.kafka.KafkaProperties;
import org.apache.kafka.common.serialization.BytesDeserializer;
import org.apache.kafka.common.serialization.BytesSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.kafka.dsl.Kafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.messaging.MessageChannel;

import java.util.Map;

@Configuration
public class IntegrationConfig {

    @Bean
    public MessageChannel messageChannel() {
        return new DirectChannel();
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, byte[]>> kafkaListenerContainerFactory(
            @Value("${kafka.bootstrap-urls:localhost:9092}") String bootstrapUrls
    ) {
        ConcurrentKafkaListenerContainerFactory<String, byte[]> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConcurrency(1);
        factory.getContainerProperties().setKafkaConsumerProperties(KafkaProperties.kafkaConsumerProperties(
                bootstrapUrls, StringDeserializer.class, BytesDeserializer.class
        ));
        return factory;
    }

    @Bean
    public ProducerFactory<String, byte[]> producerFactory(
            @Value("${kafka.bootstrap-urls:localhost:9092}") String bootstrapUrls
    ) {
        var pf = new DefaultKafkaProducerFactory<String, byte[]>(
                MapFunctions.CollectMap(
                        KafkaProperties.kafkaProducerProperties(
                                        bootstrapUrls, StringSerializer.class, BytesSerializer.class
                                )
                                .entrySet()
                                .stream()
                                .map(e -> Map.entry((String) e.getKey(), e.getValue()))
                )
        );
        return pf;
    }

    @Bean
    public KafkaTemplate<String, byte[]> kafkaTemplate(ProducerFactory<String, byte[]> factory) {
        return new KafkaTemplate<>(factory);
    }

    @Bean
    public IntegrationFlow consumeTracesFlow(KafkaTemplate<String, byte[]> kafkaTemplate,
                                             KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, byte[]>> factory,
                                             TracingBroadcasterHandle handlerTraceEventSource,
                                             TracingProperties tracingProperties) {
        return IntegrationFlow.from(
                    Kafka.inboundGateway(
                            factory.createContainer(tracingProperties.tracingTopics.toArray(String[]::new)),
                            kafkaTemplate
                    )
                )
                .handle(handlerTraceEventSource)
                .get();
    }

}