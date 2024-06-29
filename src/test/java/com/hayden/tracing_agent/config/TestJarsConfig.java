package com.hayden.tracing_agent.config;

import com.hayden.tracing_agent.TracingAgent;
import lombok.SneakyThrows;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.experimental.boot.server.exec.CommonsExecWebServerFactoryBean;

import java.util.Map;

//@TestConfiguration
public class TestJarsConfig {

//    public static void main(String[] args) {
//        SpringApplication.from(TracingAgent::main)
//                .with(TestJarsConfig.class)
//                .run(args);
//    }

//    @SneakyThrows
//    @Bean
//    static CommonsExecWebServerFactoryBean files() {
//        var b = CommonsExecWebServerFactoryBean.builder()
//                .mainClass("org.springframework.boot.loader.launch.JarLauncher")
//                .classpath(c -> c.files("../file-service/build/libs/file-service-0.0.1-SNAPSHOT.jar", "build/libs/tracing_agent-1.0.0-plain.jar"))
//                .addSystemProperties(Map.of("javaagent", "build/libs/tracing_agent-1.0.0-plain.jar"));
//
//        return b;
//    }

}
