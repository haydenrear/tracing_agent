package com.hayden.tracing_agent.messaging.support;

import com.hayden.tracing_agent.messaging.TracingTransform;
import com.hayden.tracing_agent.model.TracingMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.integration.handler.MessageProcessor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

/**
 * Passes the TracingMessages to the processors so that all nodes of an app are in the same state.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class TraceMessageHandler implements MessageProcessor<String> {

    private final TracingBroadcasterHandle traceEventSource;
    private final TracingTransform tracingTransform;

    @Override
    public String processMessage(Message<?> message) {
        return tracingTransform.from(message)
                .map(m -> {
                    traceEventSource.next(m);
                    return "Appended event.";
                })
                .orElseGet(() -> {
                    String errorMessage = "Received unknown message on tracing message handler %s."
                            .formatted(message.getPayload().getClass().getSimpleName());
                    log.warn(errorMessage);
                    return errorMessage;
                });
    }
}
