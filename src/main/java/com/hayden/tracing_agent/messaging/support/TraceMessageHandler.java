package com.hayden.tracing_agent.messaging.support;

import com.hayden.tracing_agent.messaging.TracingTransform;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
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
public class TraceMessageHandler implements MessageHandler {

    private final TracingBroadcasterHandle traceEventSource;
//    private final TracingTransform tracingTransform;

    @Override
    public void handleMessage(@NotNull Message<?> message) throws MessagingException {
//        tracingTransform.from(message)
//                .ifPresentOrElse(
//                        traceEventSource::next,
//                        () -> log.warn(
//                                "Received unknown message on tracing message handler {}.",
//                                message.getPayload().getClass().getSimpleName()
//                        )
//                );
    }
}
