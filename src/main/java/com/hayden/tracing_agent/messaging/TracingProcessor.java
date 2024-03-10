package com.hayden.tracing_agent.messaging;

import com.hayden.tracing_agent.model.TracingDecision;
import com.hayden.tracing_agent.model.TracingMessage;
import org.reactivestreams.Processor;
import org.reactivestreams.Publisher;

/**
 * Plugin point for Anomaly Detection algorithms
 */
public interface TracingProcessor  {

    /**
     * Wait until the decision is made to update the internal state, if any.
     * @param tracingDecision
     */
    void feedback(TracingDecision tracingDecision);

    void next(TracingMessage tracingMessage);

    Publisher<TracingDecision> decisions();

}