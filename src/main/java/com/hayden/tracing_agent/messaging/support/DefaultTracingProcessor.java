package com.hayden.tracing_agent.messaging.support;

import com.hayden.tracing_agent.messaging.TracingProcessor;
import com.hayden.tracing_agent.model.TracingDecision;
import com.hayden.tracing_agent.model.TracingMessage;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Component
@Slf4j
public class DefaultTracingProcessor implements TracingProcessor {

    Sinks.Many<TracingMessage> tracingMessageMany = Sinks.many().multicast().directAllOrNothing();

    @Override
    public void feedback(TracingDecision tracingDecision) {
    }

    @Override
    public void next(TracingMessage tracingMessage) {
        tracingMessageMany.emitNext(tracingMessage, (signalType, emitResult) -> {log.error("error"); return false;});
    }

    @Override
    public Publisher<TracingDecision> decisions() {
        return tracingMessageMany.asFlux()
                .flatMap(t -> t instanceof TracingDecision tr ? Flux.just(tr) : Flux.empty());
    }
}
