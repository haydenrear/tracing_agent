package com.hayden.tracing_agent;

import com.hayden.tracing_agent.config.TracingAgentConfig;
import com.hayden.tracing_agent.config.TracingProperties;
import com.hayden.tracing_agent.model.TracingDecision;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.matcher.ElementMatchers;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.context.properties.ConfigurationPropertiesBindingPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.Optional;

@Slf4j
public class TracingAgent {

    private static Instrumentation instrumentation;
    private static ApplicationContext ctx;

    @SneakyThrows
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

    public static void copyClassToCache(String className) {
    }

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

    public static void instantiateDecisionInstrumentationContext(ApplicationContext ctx) {
        TracingAgent.ctx = ctx;
    }

    public static void instrumentDecision(TracingDecision tracingDecision) {
        switch(tracingDecision) {
            case TracingDecision.AddAdvice a -> instrumentClass(a.className());
            case TracingDecision.RemoveAdvice r -> revertInstrumentation(r.className());
            default -> log.error("Received unknown tracing decision {}.", tracingDecision.getClass().getSimpleName());
        }
    }

}
