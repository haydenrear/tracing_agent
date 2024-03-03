package com.hayden.tracing_agent.service;

import com.hayden.tracing_agent.model.TracingEvent;
import org.reactivestreams.Publisher;

public interface TraceEventSource {

    Publisher<TracingEvent> tracingEvents();

}
