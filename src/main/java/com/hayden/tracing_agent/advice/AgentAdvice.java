package com.hayden.tracing_agent.advice;

import com.hayden.tracing_apt.flags.TracingFlags;
import com.hayden.tracing_apt.observation_aspects.ObservationBehavior;
import com.hayden.tracing_apt.observation_aspects.ObservationUtility;
import com.hayden.utilitymodule.MapFunctions;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.asm.Advice;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class AgentAdvice {

    public static ApplicationContext applicationContext;
    public static TracingFlags tracingFlags;

    public static void setApplicationContext(ApplicationContext ctx) {
        applicationContext = ctx;
        tracingFlags = ctx.getBean(TracingFlags.class);
    }

    @Advice.OnMethodEnter
    public static Object enterMethod(
            @Advice.This Object thiz,
            @Advice.Origin String method,
            @Advice.Origin("#s") Method origin,
            @Advice.AllArguments Object[] arguments
    ) {
        ContextHolder.observationBehavior().doObservation(
                new ObservationBehavior.AgentObservationArgs(
                        List.of(),
                        new ObservationUtility.AdviceJoinPoint(
                                new ObservationUtility.Advice(
                                        origin.getName(),
                                        ObservationUtility.JoinPointAction.Enter,
                                        ObservationUtility.JoinPointType.Agent),
                                getArgs(arguments, origin)
                        )));

        return null;
    }

    @Advice.OnMethodExit
    public static void onExitMethod(@Advice.This Object invokedObject,
                                    @Advice.Return Object returnValue,
                                    @Advice.Origin("#s") Method origin,
                                    @Advice.Enter Object state,
                                    @Advice.AllArguments Object[] arguments) {
        ContextHolder.observationBehavior().doObservation(
                new ObservationBehavior.AgentObservationArgs(
                        List.of(),
                        new ObservationUtility.AdviceJoinPoint(
                                new ObservationUtility.Advice(
                                        origin.getName(),
                                        ObservationUtility.JoinPointAction.Exit,
                                        ObservationUtility.JoinPointType.Agent),
                                getArgs(arguments, origin)
                        )));
    }

    public static Map<String, ?> getArgs(Object[] args, Method method)  {
        AtomicInteger i = new AtomicInteger();
        return MapFunctions.CollectMap(
                Arrays.stream(method.getParameters())
                        .map(p -> Map.entry(p.getName(), args[i.getAndIncrement()]))
        );
    }

}
