package com.hayden.tracing_agent.model;

public interface TracingEvent {

    record Error() {}
    record Performance() {}

}
