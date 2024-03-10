package com.hayden.tracing_agent;

import com.hayden.tracing_agent.advice.AgentAdvice;
import com.hayden.tracing_agent.advice.ContextHolder;
import com.hayden.tracing_agent.config.TracingAgentConfig;
import com.hayden.tracing_agent.config.TracingProperties;
import com.hayden.tracing_agent.model.TracingDecision;
import com.hayden.tracing_agent.model.TracingEvent;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.DefaultMethodCall;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaModule;
import org.drools.model.functions.Block0;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.context.properties.ConfigurationPropertiesBindingPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.Optional;

@Slf4j
public class TracingAgent {

    private static Instrumentation instrumentation;

    @SneakyThrows
    public static void premain(String agentArgs, Instrumentation inst) {
        log.info("Loading dynamic tracing agent with args {}.", agentArgs);
        instrumentation = inst;
        ContextHolder.getTracingService();
    }

    public static void instrumentClass(String className) {
        log.info("Instrumenting class {}.", className);
        if (copyClassToCache(className))
            new AgentBuilder.Default()
                    .type(ElementMatchers.named(className))
                    .transform((builder, typeDescription, classLoader, module, protectionDomain) -> builder
                            .visit(Advice.to(AgentAdvice.class)
                            .on(ElementMatchers.named(className)))
                    )
                    .installOn(instrumentation);
        else log.error("Could not instrument class {}.", className);
    }

    public static boolean copyClassToCache(String className) {
        try(var inputStream = TracingAgent.class.getClassLoader().getResourceAsStream("classpath:" + className)) {
            if (inputStream != null) {
                FileCopyUtils.copy(inputStream.readAllBytes(), getClassCache(className));
                return true;
            }
        } catch (IOException e) {
            log.error("Could not load {} with error {}.", className, e.getMessage());
        }
        return false;
    }

    public static Optional<byte[]> loadClassFromCache(String className) {
        File getClassCache = getClassCache(className);
        try {
            return Optional.of(FileCopyUtils.copyToByteArray(getClassCache));
        } catch (IOException e) {
            log.error("Could not load {} with error {}.", className, e.getMessage());
        }
        return Optional.empty();
    }

    @NotNull
    private static File getClassCache(String className) {
        return new File("build/cache/%s".formatted(className));
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
