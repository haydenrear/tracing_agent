package com.hayden.tracing_agent.config;

import com.hayden.tracing_agent.messaging.support.TraceMessageHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.handler.ServiceActivatingHandler;
import org.springframework.messaging.MessageChannel;

@Configuration
@Slf4j
public class IntegrationConfig {

    public static final String MESSAGE_CHANNEL = "messageChannel";

    @Bean
    public MessageChannel messageChannel() {
        return MessageChannels.direct(MESSAGE_CHANNEL)
                .getObject();
    }

    @Bean
    public ServiceActivatingHandler handleTracingMessages(
            TraceMessageHandler traceMessageHandler
    ) {
        return new ServiceActivatingHandler(traceMessageHandler);
    }

}