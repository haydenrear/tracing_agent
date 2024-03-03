package com.hayden.tracing_agent.config;

import com.hayden.tracing_agent.service.TracingMessageHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.http.dsl.Http;
import org.springframework.messaging.MessageChannel;

@Configuration
public class IntegrationConfig {

    @Bean
    public MessageChannel messageChannel() {
        return new DirectChannel();
    }

    @Bean
    @ConditionalOnBean(TracingMessageHandler.class)
    public IntegrationFlow tracingController(TracingMessageHandler tracingMessageHandler) {
        return IntegrationFlow.from(
                        Http.inboundGateway("/api/v1/trace_event")
                                .requestMapping(r -> r.methods(HttpMethod.POST))
                )
                .handle(tracingMessageHandler)
                .get();
    }

}
