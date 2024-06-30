package com.hayden.tracing_agent.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@ConfigurationProperties(prefix = "tracing-agent.kafka")
@Component
@Data
public class KafkaTracingProperties {
    List<String> tracingTopics;
}
