package com.hayden.tracing_agent.service;

import com.hayden.tracing_agent.TracingAgent;
import com.hayden.tracing_agent.messaging.TracingStream;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class DynamicTracingService {

    private final TracingStream tracingStream;

    @PostConstruct
    public void startTracing() {
        //      source                 process                aggregate             publish          decide               feedback
        // the TracingEventSource -> TracingProcessor -> TracingAggregator -> TracingStream -> TracingAggregator -> TracingProcessor
        Flux.from(tracingStream.decisions())
                .subscribe(TracingAgent::instrumentDecision);
    }


}
