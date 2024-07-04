package com.hayden.tracing_agent.messaging.support;

import com.hayden.tracing_agent.messaging.TracingTransform;
import com.hayden.tracing_agent.model.TracingMessage;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DefaultTracingTransform implements TracingTransform {
    @Override
    public Optional<TracingMessage> from(Message<?> message) {
        return Optional.of(message.getPayload())
                .flatMap(e -> e instanceof TracingMessage ? Optional.of((TracingMessage) e) : Optional.empty());
    }
}
