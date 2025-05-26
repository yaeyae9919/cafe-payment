import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    kotlin("plugin.jpa") version "1.9.25"

    id("org.springframework.boot") version "3.5.0"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.jlleitschuh.gradle.ktlint") version "12.2.0"

    idea
}

allprojects {
    group = "com.cafe"
    version = "0.1.0"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply {
        plugin("kotlin")
        plugin("kotlin-spring")
        plugin("org.springframework.boot")
        plugin("io.spring.dependency-management")
        plugin("org.jlleitschuh.gradle.ktlint")
        plugin("idea")
    }

    dependencies {
        implementation("org.springframework.boot:spring-boot-starter")
        implementation("org.jetbrains.kotlin:kotlin-reflect")

        // KotlinLogging
        implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")

        testImplementation("org.springframework.boot:spring-boot-starter-test")
        testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher")

        // Kotest
        testImplementation("io.kotest:kotest-runner-junit5:5.8.0")
        testImplementation("io.kotest:kotest-assertions-core:5.8.0")
        testImplementation("io.kotest:kotest-property:5.8.0")
    }

    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(17)
        }
    }
    kotlin {
        compilerOptions {
            freeCompilerArgs.addAll("-Xjsr305=strict")
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootJar> {
        mainClass.set("com.cafe.payment.PaymentApplicationKt") // 메인 클래스를 설정합니다
    }
}

project(":api") {
    dependencies {
        implementation(project(":service"))
        implementation(project(":library"))
        implementation("org.springframework.boot:spring-boot-starter-web")

        // Swagger/OpenAPI
        implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")
    }
    tasks.jar {
        enabled = true
    }
    tasks.bootJar {
        enabled = true
    }
}

project(":service") {
    apply(plugin = "kotlin-jpa")

    dependencies {
        implementation(project(":library"))

        // JPA
        implementation("org.springframework.boot:spring-boot-starter-data-jpa")
        runtimeOnly("com.h2database:h2")

        // Jackson for JSON serialization
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
        implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    }
}

project(":library") {}

// jar 패키징이 필요한 모듈만 사용하기 위해 default false
tasks.jar {
    enabled = false
}
tasks.bootJar {
    enabled = false
}
