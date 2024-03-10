package com.hayden.tracing_agent.advice;

import com.hayden.tracing.observation_aspects.ObservationBehavior;
import com.hayden.utilitymodule.MapFunctions;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.asm.Advice;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class AgentAdvice {

    @Advice.OnMethodEnter
    public static void enterMethod(@Advice.This Object invokedObject,
                                   @Advice.AllArguments Object[] arguments,
                                   @Advice.Origin("#s") Method origin,
                                   @Advice.Origin("#m") String methodName) {
        ContextHolder.observationBehavior().doObservation(
                new ObservationBehavior.AgentObservationArgs(
                        methodName,
                        List.of(),
                        new ObservationBehavior.AgentObservationArgs.AdviceJoinPoint(
                                ObservationBehavior.AgentObservationArgs.AgentAdvice.Enter,
                                getArgs(arguments, origin)
                        )
                ));
    }

    public static Map<String, ?> getArgs(Object[] args, Method method)  {
        AtomicInteger i = new AtomicInteger();
        return MapFunctions.CollectMap(
                Arrays.stream(method.getParameters())
                        .map(p -> Map.entry(p.getName(), args[i.getAndIncrement()]))
        );
    }

    @Advice.OnMethodExit
    public static void onExitMethod(@Advice.This Object invokedObject,
                                    @Advice.Return Object returnValue,
                                    @Advice.Enter Object state) {
    }

}
