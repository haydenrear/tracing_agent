package com.hayden.tracing_agent.messaging;

import com.hayden.tracing_agent.model.TracingMessage;
import org.reactivestreams.Publisher;

public interface TracingBroadcaster {

    void next(TracingMessage tracingMessage);

    Publisher<TracingMessage> messages();

}
