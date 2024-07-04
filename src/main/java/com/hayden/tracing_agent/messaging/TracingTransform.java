package com.hayden.tracing_agent.messaging;


import com.hayden.tracing_agent.model.TracingMessage;
import org.springframework.messaging.Message;

import java.util.Optional;

public interface TracingTransform {

    Optional<TracingMessage> from(Message<?> message);

}
