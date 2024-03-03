plugins {
    id("com.hayden.no-main-class")
    id("com.hayden.observable-app")
}

tasks.register("prepareKotlinBuildScriptModel")

dependencies {
    implementation("org.springframework.boot:spring-boot-docker-compose")
    implementation("io.opentelemetry.instrumentation:opentelemetry-logback-appender-1.0:2.1.0-alpha")
    implementation("io.opentelemetry.instrumentation:opentelemetry-instrumentation-api:2.1.0")
    implementation("io.opentelemetry.instrumentation:opentelemetry-spring-boot-starter:1.22.1-alpha")
    implementation("io.opentelemetry.instrumentation:opentelemetry-instrumentation-annotations:2.1.0")
    implementation("io.opentelemetry.javaagent:opentelemetry-javaagent:2.0.0")
    implementation("io.opentelemetry.instrumentation:opentelemetry-jdbc:2.1.0-alpha")
    implementation("io.micrometer:context-propagation:1.1.1")

    implementation("io.micrometer:micrometer-tracing-bridge-brave")
    runtimeOnly("io.micrometer:micrometer-registry-prometheus")
    runtimeOnly("io.micrometer:micrometer-core")
    runtimeOnly("io.micrometer:micrometer-registry-otlp")

    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.liquibase:liquibase-core")


    compileOnly("org.projectlombok:lombok")
    runtimeOnly("org.postgresql:postgresql")
    implementation("net.bytebuddy:byte-buddy:1.14.12")
    implementation("net.bytebuddy:byte-buddy-agent:1.14.12")


    developmentOnly("org.springframework.boot:spring-boot-devtools")

    annotationProcessor("org.projectlombok:lombok")

    testImplementation("org.springframework.boot:spring-boot-starter-test")

    implementation(project(":utilitymodule"))

    annotationProcessor(project(":tracing_apt")) {
        exclude("org.junit")
    }
}

tasks.bootJar {
    enabled=false
}

tasks.jar {
    manifest {
        attributes["Premain-Class"]  = "com.hayden.tracing_agent.TracingAgent"
    }
}