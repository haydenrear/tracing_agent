package com.hayden.tracing_agent.messaging.support;

import com.hayden.tracing_agent.messaging.TracingProcessor;
import com.hayden.tracing_agent.model.TracingDecision;
import com.hayden.tracing_agent.model.TracingMessage;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
public class DefaultTracingProcessor implements TracingProcessor {
    @Override
    public void feedback(TracingDecision tracingDecision) {

    }

    @Override
    public void next(TracingMessage tracingMessage) {

    }

    @Override
    public Publisher<TracingDecision> decisions() {
        return Flux.empty();
    }
}
