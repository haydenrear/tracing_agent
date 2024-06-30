package com.hayden.tracing_agent.config;

import com.hayden.utilitymodule.ArrayUtilUtilities;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.integration.dsl.*;
import org.springframework.integration.handler.ServiceActivatingHandler;
import org.springframework.integration.http.dsl.Http;

@Configuration
@Slf4j
@Profile("http")
public class HttpIntegrationConfig {


    @Bean
    public IntegrationFlow testFlow(
            HttpTracingProperties tracingProperties,
            ServiceActivatingHandler handleTracingMessages
    ) {
        return IntegrationFlow.from(
                    Http.inboundGateway(ArrayUtilUtilities.toArray(tracingProperties.path, String[]::new))
                        .requestMapping(r -> ArrayUtilUtilities.toArray(tracingProperties.methods, HttpMethod[]::new))
                )
                .handle(handleTracingMessages)
                .get();
    }


}