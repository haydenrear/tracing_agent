package com.hayden.tracing_agent;

import com.hayden.tracing.NoAgent;
import com.hayden.tracing_agent.advice.AgentAdvice;
import com.hayden.tracing_agent.advice.ContextHolder;
import com.hayden.tracing_agent.model.TracingDecision;
import com.hayden.tracing_agent.service.DynamicTracingService;
import jakarta.annotation.Nullable;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.jar.asm.commons.ClassRemapper;
import net.bytebuddy.matcher.ElementMatchers;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.io.File;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.security.ProtectionDomain;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@SpringBootApplication
@EnableWebMvc
public class TracingAgent {

    private static Instrumentation instrumentation;
    private static Instrumentation byteBuddyInstrumentation;
    private static DynamicTracingService dynamicTracingService;

    @SneakyThrows
    public static void premain(String agentArgs, Instrumentation inst) {
        log.info("Loading dynamic tracing agent with args {} and {}.", agentArgs, inst);
        var tracing = ContextHolder.getTracingService();
        instrumentation = inst;
        byteBuddyInstrumentation = ByteBuddyAgent.install();
    }

    public static void instrumentClass(String className) {
        instrumentClass(className, null);
    }

    public static void instrumentClass(String className, @Nullable String methodName) {
        log.info("Instrumenting class {}.", className);
        var methodMatcher = Optional.ofNullable(methodName)
                .map(m -> ElementMatchers.not(ElementMatchers.isAnnotatedWith(NoAgent.class))
                        .and(ElementMatchers.named(m)))
                .orElseGet(ElementMatchers::any);
        new AgentBuilder.Default()
                // TODO: Could add @Stateless somehow with spring ctx, probably not...
                .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
                .with(AgentBuilder.Listener.StreamWriting.toSystemOut())
                .type(ElementMatchers.named(className))
                .and(ElementMatchers.not(ElementMatchers.isAnnotatedWith(NoAgent.class)))
                .transform((builder, typeDescription, classLoader, module, protectionDomain) -> builder
                        .method(methodMatcher)
                        .intercept(Advice.to(AgentAdvice.class))
                )
                .installOn(byteBuddyInstrumentation);
    }


    public static void revertInstrumentation(String toRevert) {
        // handled in tracing decision in context...
    }

    public static void instrumentDecision(TracingDecision tracingDecision) {
        switch(tracingDecision) {
            case TracingDecision.AddAdvice a -> instrumentClass(a.className());
            case TracingDecision.RemoveAdvice r -> revertInstrumentation(r.className());
            default -> log.error("Received unknown tracing decision {}.", tracingDecision.getClass().getSimpleName());
        }
    }

}
