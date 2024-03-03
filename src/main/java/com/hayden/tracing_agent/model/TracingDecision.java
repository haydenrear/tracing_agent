package com.hayden.tracing_agent.model;

import com.hayden.tracing_agent.service.TracingMessageHandler;
import com.hayden.tracing_agent.service.TracingProcessor;

public interface TracingDecision extends TracingMessage {

    record AddAdvice(String className) implements TracingDecision {}

    record RemoveAdvice(String className) implements TracingDecision{}


}
