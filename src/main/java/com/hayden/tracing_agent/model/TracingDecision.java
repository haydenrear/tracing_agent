package com.hayden.tracing_agent.model;


public interface TracingDecision extends TracingMessage {

    String className();
    String functionName();

    default String tracingId() {
        return "%s.%s".formatted(className(), functionName());
    }

    record AddAdvice(String className, String functionName) implements TracingDecision {}

    record RemoveAdvice(String className, String functionName) implements TracingDecision {}

}
