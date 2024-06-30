package com.hayden.tracing_agent.messaging.support;

import com.hayden.tracing_agent.messaging.TracingAggregator;
import com.hayden.tracing_agent.model.TracingDecision;
import jakarta.annotation.Nullable;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;


@Component
public class DefaultTracingAggregator implements TracingAggregator {
    @Override
    public Collection<TracingDecision> decide(Collection<TracingDecision> tracingDecisions) {
        var grouped = tracingDecisions.stream().collect(Collectors.groupingBy(TracingDecision::tracingId));
        return grouped.entrySet().stream()
                .flatMap(n -> {
                    var toAdd = n.getValue().stream().collect(Collectors.partitioningBy(e -> e instanceof TracingDecision.AddAdvice));
                    if (getSize(toAdd.get(true)) > getSize(toAdd.get(false))) {
                        return toAdd.get(true).stream();
                    } else {
                        return toAdd.get(false).stream();
                    }
                })
                .toList();
    }

    public int getSize(@Nullable Collection<TracingDecision> toAdd) {
        return Optional.ofNullable(toAdd).map(Collection::size).orElse(0);
    }
}
