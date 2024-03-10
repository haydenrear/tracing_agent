package com.hayden.tracing_agent.advice;

import com.hayden.tracing.observation_aspects.MonitoringTypes;
import com.hayden.tracing.observation_aspects.ObservationBehavior;
import com.hayden.tracing_agent.service.DynamicTracingService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class ContextHolder {

    private static final ReentrantLock reentrantLock = new ReentrantLock();
    private static ApplicationContext applicationContext;

    public static ApplicationContext getApplicationContext() {
        if (applicationContext == null && !reentrantLock.isLocked()) {
            reentrantLock.lock();
            setContext();
            reentrantLock.unlock();
        } else if (applicationContext == null) {
            reentrantLock.lock();
            setContext();
            reentrantLock.unlock();
        }

        return applicationContext;
    }

    private static void setContext() {
        if (applicationContext == null)
            applicationContext = new AnnotationConfigApplicationContext("com.hayden.tracing", "com.hayden.tracing_agent", "com.hayden.tracing_apt");
    }

    public static ObservationBehavior observationBehavior() {
        return getApplicationContext().getBean(ObservationBehavior.class);
    }

    public static DynamicTracingService getTracingService() {
        return getApplicationContext().getBean(DynamicTracingService.class);
    }

    public static List<MonitoringTypes> getMonitoringTypes(Method method, Object thisParameter) {
        return new ArrayList<>();
    }

}
