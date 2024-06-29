package com.hayden.tracing_agent.advice;

import com.hayden.tracing.flags.TracingFlags;
import com.hayden.tracing.observation_aspects.ObservationBehavior;
import com.hayden.utilitymodule.MapFunctions;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import org.springframework.context.ApplicationContext;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class AgentAdvice {

    public static ApplicationContext applicationContext;
    public static TracingFlags tracingFlags;

    public static void setApplicationContext(ApplicationContext ctx) {
        applicationContext = ctx;
        tracingFlags = ctx.getBean(TracingFlags.class);
    }

    //    @Advice.OnMethodEnter
//    public static long enterMethod(
//            @Advice.Origin String method
//    ) {
//        ContextHolder.observationBehavior().doObservation(
//                new ObservationBehavior.AgentObservationArgs(
//                        methodName,
//                        List.of(),
//                        new ObservationBehavior.AgentObservationArgs.AdviceJoinPoint(
//                                ObservationBehavior.AgentObservationArgs.AgentAdvice.Enter,
//                                getArgs(arguments, origin)
//                        )
//                ));
//    }
//
//    public static Map<String, ?> getArgs(Object[] args, Method method)  {
//        AtomicInteger i = new AtomicInteger();
//        return MapFunctions.CollectMap(
//                Arrays.stream(method.getParameters())
//                        .map(p -> Map.entry(p.getName(), args[i.getAndIncrement()]))
//        );
//    }
//
//    @Advice.OnMethodExit
//    public static void onExitMethod(@Advice.This Object invokedObject,
//                                    @Advice.Return Object returnValue,
//                                    @Advice.Enter Object state) {
//        log.info("Exiting ...");
//        System.out.println("esxiting");
//    }

    @Advice.OnMethodEnter
    static long invokeBeforeEnterMethod(
            @Advice.This Object thiz,
            @Advice.Origin String method,
            @Advice.Origin("#s") Method origin,
            @Advice.AllArguments Object[] arguments
    ) {
        if (tracingFlags.isEnabled(thiz.getClass().getName())) {
            System.out.printf("is enabled!%n");
//            return System.currentTimeMillis();
        }
        System.out.printf("Method invoked before enter method by: %s and %s and %s%n", method, thiz.getClass(), origin);

        return System.currentTimeMillis();
//        return 0L;
    }

    @Advice.OnMethodExit
    static void invokeAfterExitMethod(
            @Advice.Origin String method,
            @Advice.Enter long startTime,
            @Advice.Return Object returnValue
    ) {
        System.out.println("Method " + method + " took " + (System.currentTimeMillis() - startTime) + "ms");
        System.out.printf("%s is the ret%n", returnValue);
    }

    @SneakyThrows
    @RuntimeType
    public static Object intercept(@Origin Method method,
                                   @SuperCall Callable<?> callable) {
        long start = System.currentTimeMillis();
        System.out.println("intercepted");
        log.info("intercepting on {}.", method.getName());
        try {
            return callable.call();
        } finally {
            System.out.println(method + " took " + (System.currentTimeMillis() - start));
        }
    }

}
