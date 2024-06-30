package com.hayden.tracing_agent;

import com.hayden.tracing.NoAgent;
import com.hayden.tracing_agent.advice.AgentAdvice;
import com.hayden.tracing_agent.advice.ContextHolder;
import com.hayden.tracing_agent.service.DynamicTracingService;
import jakarta.annotation.Nullable;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.Transformer;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.jar.asm.ClassVisitor;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.jar.asm.Opcodes;
import net.bytebuddy.jar.asm.commons.ClassRemapper;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.pool.TypePool;
import net.bytebuddy.utility.JavaModule;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.Optional;

import static net.bytebuddy.matcher.ElementMatchers.named;

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
        dynamicTracingService = ContextHolder.initTracingService();
        instrumentation = inst;
        byteBuddyInstrumentation = ByteBuddyAgent.install();
    }

    public static void instrumentClass(String className) {
        instrumentClass(className, null);
    }

    @SneakyThrows
    public static void instrumentClass(String className, @Nullable String methodName) {
        log.info("Instrumenting class {}.", className);
        var c = Class.forName(className);
        Optional.ofNullable(methodName)
                .map(m -> ElementMatchers.not(ElementMatchers.isAnnotatedWith(NoAgent.class))
                        .and(named(m))
                )
                .ifPresentOrElse(
                        methodMatcher -> {
                            try (var b = new ByteBuddy()
                                    .redefine(c)
                                    .visit(Advice.to(AgentAdvice.class).on(methodMatcher))
                                    .make()) {
                                b.load(c.getClassLoader(), ClassReloadingStrategy.of(byteBuddyInstrumentation));
                            } catch (Exception e) {
                                log.error("Error instrumenting class: {}.", e.getMessage());
                            }
                        },
                        () -> log.error("could not instrument class, no method name provided.")
                );
    }


    public static void registerRevertInstrumentation(String toRevert) {
        // handled in tracing decision in context...
    }

    public static void registerRevertInstrumentation(String toRevert, String functionName) {
        // handled in tracing decision in context...
    }

}
