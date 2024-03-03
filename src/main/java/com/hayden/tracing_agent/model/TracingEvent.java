package com.hayden.tracing_agent.model;

public interface TracingEvent extends TracingMessage {

    @Override
    default boolean isLocal() {
        return true;
    }

    record Trace() {}
    record Debug() {}
    record Info() {}
    record Warn() {}
    record Error() {}
    record Performance() {}
    record AddTracing() {}
    record RemoveTracing() {}

}