import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.2.1"
	id("io.spring.dependency-management") version "1.1.4"
	id("org.graalvm.buildtools.native") version "0.9.28"
	id("com.netflix.dgs.codegen") version "6.0.3"
	kotlin("jvm") version "1.9.21"
	kotlin("plugin.spring") version "1.9.21"
}

group = "io.graphql.juliuskrah"
version = "0.0.1-SNAPSHOT"
var mongockVersion = "5.4.0"

java {
	sourceCompatibility = JavaVersion.VERSION_21
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-graphql")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("com.graphql-java:graphql-java-extended-scalars:19.0")
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
	implementation("io.mongock:mongodb-reactive-driver:$mongockVersion")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
	implementation("io.mongock:mongock-springboot:$mongockVersion")
	developmentOnly("org.springframework.boot:spring-boot-docker-compose")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.boot:spring-boot-testcontainers")
	testImplementation("org.testcontainers:junit-jupiter")
	testImplementation("org.testcontainers:mongodb")
	testImplementation("io.projectreactor:reactor-test")
	testImplementation("org.mockito.kotlin:mockito-kotlin:5.1.0")
	testImplementation("org.springframework.graphql:spring-graphql-test")
	testImplementation("org.springframework:spring-core-test")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs += "-Xjsr305=strict"
		jvmTarget = "21"
	}
}

tasks.generateJava {
	schemaPaths.add("${projectDir}/src/main/resources/graphql")
	packageName = "com.example.graph.generated"
	typeMapping = mutableMapOf("URL" to "java.net.URL")
}

tasks.withType<Test> {
	useJUnitPlatform()
	jvmArgs("-Dspring.test.aot.processing.failOnError=false")
}

graalvmNative {
	binaries.all {
		buildArgs.add("-H:IncludeLocales=de,en") // see https://www.graalvm.org/latest/reference-manual/native-image/dynamic-features/Resources/#locales
	}
}
