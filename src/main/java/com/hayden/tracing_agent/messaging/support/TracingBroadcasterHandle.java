package com.hayden.tracing_agent.messaging.support;

import com.hayden.tracing_agent.messaging.TracingBroadcaster;
import com.hayden.tracing_agent.model.TracingMessage;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Sinks;

@Component
public class TracingBroadcasterHandle implements TracingBroadcaster {

    private final Sinks.Many<TracingMessage> tracingMessages = Sinks.many().multicast().directAllOrNothing();

    public void next(TracingMessage tracingMessage) {
        tracingMessages.emitNext(tracingMessage, (signalType, emitResult) -> false);
    }

    @Override
    public Publisher<TracingMessage> messages() {
        return tracingMessages.asFlux();
    }

}
