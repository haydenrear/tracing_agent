package com.hayden.tracing_agent.messaging;

import com.hayden.tracing_agent.model.TracingDecision;
import com.hayden.tracing_agent.model.TracingMessage;
import org.reactivestreams.Publisher;

public interface TracingStream {

    void register(TracingProcessor tracingProcessor);

    Publisher<TracingDecision> decisions();

}
