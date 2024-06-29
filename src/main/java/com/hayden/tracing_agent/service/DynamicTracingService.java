package com.hayden.tracing_agent.service;

import com.hayden.tracing.flags.TracingFlags;
import com.hayden.tracing_agent.TracingAgent;
import com.hayden.tracing_agent.messaging.TracingStream;
import com.hayden.tracing_agent.model.TracingDecision;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
@Slf4j
public class DynamicTracingService {

    private final TracingStream tracingStream;
    private final TracingFlags tracingFlags;

    @PostConstruct
    public void startTracing() {
        //      source                 process                aggregate             publish          decide               feedback
        // the TracingEventSource -> TracingProcessor -> TracingAggregator -> TracingStream -> TracingAggregator -> TracingProcessor
        Flux.from(tracingStream.decisions())
                .map(t -> {
                    switch(t) {
                        case TracingDecision.AddAdvice a -> tracingFlags.add(a.className());
                        case TracingDecision.RemoveAdvice r -> tracingFlags.remove(r.className());
                        default -> log.error("Received unknown tracing decision {}.", t.getClass().getSimpleName());
                    }
                    return t;
                })
                .subscribe(TracingAgent::instrumentDecision);
    }


}
