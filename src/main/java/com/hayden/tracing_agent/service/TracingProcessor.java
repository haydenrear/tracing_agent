package com.hayden.tracing_agent.service;

import com.hayden.tracing_agent.model.TracingDecision;
import com.hayden.tracing_agent.model.TracingEvent;
import org.reactivestreams.Processor;

/**
 * Plugin point for Anomaly Detection algorithms
 */
public interface TracingProcessor extends Processor<TracingEvent, TracingDecision> {

    /**
     * Wait until the decision is made to update the internal state, if any.
     * @param tracingDecision
     */
    void feedback(TracingDecision tracingDecision);

}
