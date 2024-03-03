package com.hayden.tracing_agent.service;

import com.hayden.tracing_agent.model.TracingDecision;
import com.hayden.tracing_agent.model.TracingEvent;
import org.reactivestreams.Publisher;

public interface TracingStream {

    void register(TracingProcessor tracingProcessor);

    void register(TracingAggregator tracingAggregator);

    Publisher<TracingDecision> tracingStream();

    void next(TracingDecision next);

}
