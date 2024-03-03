package com.hayden.tracing_agent.model;

import com.hayden.tracing_agent.service.TracingProcessor;

public interface TracingDecision {

    record AddAdvice(String className) implements TracingDecision {}

    record RemoveAdvice(String className) implements TracingDecision{}


}
