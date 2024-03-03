package com.hayden.tracing_agent.model;

public interface TracingMessage {
    default boolean isLocal() {
        return true;
    }
}
