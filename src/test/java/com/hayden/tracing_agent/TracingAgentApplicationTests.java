package com.hayden.tracing_agent;

import com.hayden.tracing_agent.config.TestJarsConfig;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest(classes = TestJarsConfig.class)
public class TracingAgentApplicationTests {

    @SneakyThrows
    @Test
    void contextLoads() {
        Thread.sleep(10000);
    }

}
