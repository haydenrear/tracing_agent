package com.hayden.tracing_agent.messaging.support;

import com.hayden.tracing_agent.config.TracingProperties;
import com.hayden.tracing_agent.messaging.TracingAggregator;
import com.hayden.tracing_agent.messaging.TracingProcessor;
import com.hayden.tracing_agent.messaging.TracingStream;
import com.hayden.tracing_agent.messaging.support.TracingBroadcasterHandle;
import com.hayden.tracing_agent.model.TracingDecision;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TracingStreamHandle implements TracingStream {

    private final List<TracingProcessor> processors = new ArrayList<>();

    private final Sinks.Many<TracingDecision> tracingDecisions = Sinks.many().multicast().directAllOrNothing();

    @Delegate
    private final TracingBroadcasterHandle tracingBroadcaster;
    private final TracingProperties tracingProperties;
//    private final TracingAggregator aggregators;

    @PostConstruct
    public void setup() {
//        Flux.from(tracingBroadcaster.messages())
//                .subscribe(tm -> processors.forEach(tp -> tp.next(tm)));
//        Flux.fromIterable(processors)
//                .flatMap(t -> Flux.from(t.decisions()))
//                .buffer(tracingProperties.getSize())
//                .map(aggregators::decide)
//                .doOnNext(d -> processors.forEach(tp -> tp.feedback(d)))
//                .subscribe(decision -> tracingDecisions.emitNext(decision, (t1, tw) -> true));
    }

    @Override
    public void register(TracingProcessor tracingProcessor) {
        processors.add(tracingProcessor);
    }

    @Override
    public Publisher<TracingDecision> decisions() {
        return tracingDecisions.asFlux();
    }

}
