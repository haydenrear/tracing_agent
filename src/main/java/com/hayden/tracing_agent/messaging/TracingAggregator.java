package com.hayden.tracing_agent.messaging;

import com.hayden.tracing_agent.model.TracingDecision;

import java.util.Collection;

public interface TracingAggregator {


    TracingDecision decide(Collection<TracingDecision> tracingDecisions);

}
