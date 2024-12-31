plugins {
	java
	id("org.springframework.boot") version "3.4.0"
	id("io.spring.dependency-management") version "1.1.6"
	id("org.graalvm.buildtools.native") version "0.10.3"
}

group = "com.github.vanroy"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

extra["kafka.version"] = "3.9.0"

dependencies {

	compileOnly("org.projectlombok:lombok")
	compileOnly("org.jspecify:jspecify:1.0.0")

	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
	implementation("org.springframework.boot:spring-boot-starter-data-redis")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.flywaydb:flyway-core")
	implementation("org.flywaydb:flyway-database-postgresql")
	implementation("org.springframework.kafka:spring-kafka")
//	implementation("io.cloudevents:cloudevents-spring:4.0.1")
	implementation("io.cloudevents:cloudevents-kafka:4.0.1")
	implementation("io.cloudevents:cloudevents-json-jackson:4.0.1")
	implementation("io.cloudevents:cloudevents-avro-compact:4.0.1")
	implementation("software.amazon.event.ruler:event-ruler:1.8.1")
	implementation("com.jayway.jsonpath:json-path:2.9.0")

	implementation("org.postgresql:postgresql")

	developmentOnly("org.springframework.boot:spring-boot-docker-compose")

	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
	annotationProcessor("org.projectlombok:lombok")

	testImplementation("org.springframework.boot:spring-boot-docker-compose")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.kafka:spring-kafka-test")
	testImplementation("io.rest-assured:rest-assured")
	testImplementation("org.assertj:assertj-db:3.0.0")
	testImplementation("org.wiremock:wiremock:3.10.0")

	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
