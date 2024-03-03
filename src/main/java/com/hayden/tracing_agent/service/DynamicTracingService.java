package com.hayden.tracing_agent.service;

import com.hayden.tracing_agent.TracingAgent;
import com.hayden.tracing_agent.config.TracingProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class DynamicTracingService {

    private final TracingStream tracingStream;
    // will be an aggregate - many decisions
    // aggregator makes final decision and provides feedback
    private final TracingProcessor tracingProcessor;
    private final TraceEventSource traceEventSource;
    private final TracingAggregator tracingAggregator;
    private final TracingProperties tracingProperties;

    @PostConstruct
    public void startTracing() {
        //      source                 process                aggregate             publish          decide               feedback
        // the TracingEventSource -> TracingProcessor -> TracingAggregator -> TracingStream -> TracingAggregator -> TracingProcessor
        Flux.from(traceEventSource.tracingEvents())
                .subscribe(tracingProcessor::onNext);

        Flux.from(tracingProcessor)
                .subscribe(tracingStream::next);

        Flux.from(tracingStream.tracingStream())
                .buffer(tracingProperties.getSize())
                .map(tracingAggregator::decide)
                .doOnNext(tracingProcessor::feedback)
                .subscribe(TracingAgent::instrumentDecision);
    }

}
