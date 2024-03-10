package com.hayden.tracing_agent.model;

import com.hayden.tracing_agent.service.TracingMessageHandler;

public interface TracingDecision extends TracingMessage {

    record AddAdvice(String className) implements TracingDecision {}

    record RemoveAdvice(String className) implements TracingDecision{}


}
