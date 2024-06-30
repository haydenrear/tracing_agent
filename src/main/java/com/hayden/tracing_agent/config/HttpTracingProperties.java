package com.hayden.tracing_agent.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import java.util.List;

@ConfigurationProperties(prefix = "tracing-agent.http")
@Component
@Data
public class HttpTracingProperties {
    List<String> path;
    List<HttpMethod> methods;
}
