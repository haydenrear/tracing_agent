package com.hayden.tracing_agent;

import com.hayden.tracing_agent.config.TestJarsConfig;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@SpringBootTest(classes = TestJarsConfig.class)
public class TracingAgentApplicationTests {

    @SneakyThrows
    @Test
    void contextLoads() {
        Thread.sleep(10000);
    }

}
