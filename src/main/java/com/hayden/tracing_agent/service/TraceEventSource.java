package com.hayden.tracing_agent.service;

import com.hayden.tracing_agent.model.TracingMessage;
import org.reactivestreams.Publisher;

public interface TraceEventSource {

    Publisher<TracingMessage> tracingEvents();

}
