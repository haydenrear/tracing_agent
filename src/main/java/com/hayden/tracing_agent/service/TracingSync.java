package com.hayden.tracing_agent.service;

import com.hayden.tracing_agent.model.TracingDecision;
import com.hayden.tracing_agent.model.TracingMessage;

public interface TracingSync {

    /**
     * Pushes to the database which is then read by the services at time intervals, allowing for the required consistency.
     * @param tracingMessage
     */
    boolean push(TracingMessage tracingMessage);

}
