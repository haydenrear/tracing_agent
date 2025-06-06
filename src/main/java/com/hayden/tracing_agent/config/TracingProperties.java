package com.hayden.tracing_agent.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "tracing-agent")
@Component
@Data
public class TracingProperties {

    int size;

}
