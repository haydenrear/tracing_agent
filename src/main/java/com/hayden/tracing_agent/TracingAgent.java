package com.hayden.tracing_agent;

import com.hayden.tracing_agent.model.TracingDecision;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.Optional;

@Slf4j
public class TracingAgent {

    private static Instrumentation instrumentation;

    public static void premain(String agentArgs, Instrumentation inst) {
        log.info("Loading dynamic tracing agent with args {}.", agentArgs);
        instrumentation = inst;
    }

    public static void instrumentClass(String className) {
        log.info("Instrumenting class {}.", className);
        copyClassToCache(className);
        new AgentBuilder.Default()
                .type(ElementMatchers.named(className))
                .transform(new AgentBuilder.Transformer.ForAdvice(Advice.withCustomMapping()))
                .installOn(instrumentation);
    }

    public static void copyClassToCache(String className) {}

    public static Optional<byte[]> loadClassFromCache(String className) {
        return Optional.of(new byte[]{});
    }

    public static void revertInstrumentation(String toRevert) {
        // Use the Instrumentation API to redefine/reload the class
        // with the original bytecode
        log.info("Reverting instrumentation for class {}.", toRevert);
        instrumentation.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                                    ProtectionDomain protectionDomain, byte[] classfileBuffer) {
                return loadClassFromCache(toRevert)
                        .orElse(classfileBuffer);
            }
        }, true);
    }

    public static void instrumentDecision(TracingDecision tracingDecision) {
        switch(tracingDecision) {
            case TracingDecision.AddAdvice a -> instrumentClass(a.className());
            case TracingDecision.RemoveAdvice r -> revertInstrumentation(r.className());
            default -> log.error("Received unknown tracing decision {}.", tracingDecision.getClass().getSimpleName());
        }
    }

}
