import com.hayden.haydenbomplugin.BuildSrcVersionCatalogCollector

plugins {
    id("com.hayden.no-main-class")
    id("com.hayden.messaging")
    id("com.hayden.observable-app")
    id("com.hayden.spring-app")
    id("com.hayden.web-app")
}

tasks.register("prepareKotlinBuildScriptModel")

val vC = project.extensions.getByType(BuildSrcVersionCatalogCollector::class.java)

dependencies {
    implementation("org.springframework.boot:spring-boot-docker-compose")
    implementation("org.springframework.boot:spring-boot-starter-web")

    vC.bundles.opentelemetryBundle.inBundle()
        .map { implementation(it) }

    implementation("io.opentelemetry.javaagent:opentelemetry-javaagent:2.8.0")

    implementation("io.micrometer:context-propagation:1.1.1")

    testImplementation("org.springframework.experimental.boot:spring-boot-testjars:0.0.1")
    testImplementation("org.springframework.experimental.boot:spring-boot-testjars:0.0.1") {
        capabilities {
            requireCapability("org.springframework.experimental.boot:spring-boot-testjars-maven")
        }
    }

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


//    developmentOnly("org.springframework.boot:spring-boot-devtools")

    annotationProcessor("org.projectlombok:lombok")

    testImplementation("org.springframework.boot:spring-boot-starter-test")

    implementation(project(":utilitymodule"))

    implementation(project(":tracing_apt"))
    annotationProcessor(project(":tracing_apt")) {
        exclude("org.junit")
    }

    implementation("org.springframework.boot:spring-boot-starter-integration")
    implementation("org.springframework.integration:spring-integration-kafka")
    implementation("org.springframework.integration:spring-integration-core")
    implementation("org.springframework.integration:spring-integration-http")


}

tasks.bootJar {
    enabled=false
}

tasks.jar {
    manifest {
        attributes["Premain-Class"]  = "com.hayden.tracing_agent.TracingAgent"
    }
}