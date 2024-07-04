package com.hayden.tracing_agent.advice;

import com.hayden.tracing_apt.observation_aspects.MonitoringTypes;
import com.hayden.tracing_apt.observation_aspects.ObservationBehavior;
import com.hayden.tracing_agent.service.DynamicTracingService;
import com.hayden.utilitymodule.stream.StreamUtil;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class ContextHolder {

    private static final String TRACING_AGENT = "tracing_agent";
    private static final String[] TRACING_PACKAGES = new String[] {"com.hayden.tracing", "com.hayden.tracing_agent", "com.hayden.tracing_apt"};


    private static final ConcurrentHashMap<String, ApplicationContext> applicationContext = new ConcurrentHashMap<>();

    public static ApplicationContext getApplicationContext() {
        return applicationContext.compute(TRACING_AGENT, (k, p) -> Optional.ofNullable(p)
                .orElseGet(ContextHolder::setContext));
    }

    private static ApplicationContext setContext() {
        if (!applicationContext.containsKey(TRACING_AGENT)) {
            var pkgs = Optional.ofNullable(System.getenv().get("TRACING_PROVIDERS"))
                    .map(s -> StreamUtil.StreamBuilderDelegate.builder()
                            .addAllStreams(Arrays.stream(s.split(",")), Arrays.stream(TRACING_PACKAGES))
                            .build()
                            .map(Object::toString))
                    .or(() -> Optional.of(Arrays.stream(TRACING_PACKAGES)))
                    .stream()
                    .flatMap(s -> s)
                    .toArray(String[]::new);

            var applicationContext = new AnnotationConfigServletWebServerApplicationContext(pkgs);
            AgentAdvice.setApplicationContext(applicationContext);
            return applicationContext;
        }

        return applicationContext.get(TRACING_AGENT);

    }

    public static ObservationBehavior observationBehavior() {
        return getApplicationContext().getBean(ObservationBehavior.class);
    }

    public static DynamicTracingService initTracingService() {
        return getApplicationContext().getBean(DynamicTracingService.class);
    }

    public static List<MonitoringTypes> getMonitoringTypes(Method method, Object thisParameter) {
        return new ArrayList<>();
    }

}
