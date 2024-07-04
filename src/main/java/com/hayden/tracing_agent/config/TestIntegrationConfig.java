package com.hayden.tracing_agent.config;

import com.hayden.tracing_agent.TracingAgent;
import com.hayden.utilitymodule.ArrayUtilUtilities;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.autoconfigure.tracing.TracingProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.handler.MessageProcessor;
import org.springframework.integration.handler.ServiceActivatingHandler;
import org.springframework.integration.http.dsl.Http;
import org.springframework.messaging.Message;

@Configuration
@Slf4j
@Profile("test")
public class TestIntegrationConfig {


    @Bean
    public IntegrationFlow testFlow(
            TracingProperties tracingProperties
    ) {
        return IntegrationFlow.from(
                        Http.inboundGateway("/test-integration")
                                .requestMapping(r -> r.methods(HttpMethod.GET))
                                .replyChannel(IntegrationConfig.MESSAGE_CHANNEL)
                )
                .handle(new ServiceActivatingHandler(message -> {
                    log.info("Handling!");
                    TracingAgent.instrumentClass("com.hayden.tracing.TestClass", "test");
                    return "yes!";
                }))
                .get();
    }


}