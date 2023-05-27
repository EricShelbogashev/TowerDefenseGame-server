plugins {
    id("java")
    id("org.openapi.generator") version "5.4.0"
}

group = "ru.nsu.shelbogashev.tdgserver"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

extra["openapiVersion"] = "6.0.1"
extra["openapiUiVersion"] = "1.6.11"
extra["openapitoolsVersion"] = "0.2.3"
extra["javaxVersion"] = "1.3.2"

dependencies {
    /* Spring  */
    implementation("org.springframework.boot:spring-boot-starter-tomcat:3.0.5")
    implementation("org.springframework:spring-messaging:6.0.8")
    implementation("org.springframework.boot:spring-boot-starter-websocket:3.0.5")

    implementation("org.springframework.boot:spring-boot-starter-security:3.0.5")

    implementation("org.springframework.security:spring-security-jwt:1.1.1.RELEASE")
    implementation("org.springframework.boot:spring-boot-starter-web:3.0.5")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.0.5")
    implementation("org.springframework.boot:spring-boot-starter-reactor-netty:3.0.5")

    /* Utils */
    implementation("org.jetbrains:annotations:24.0.0")
    implementation("javax.annotation:javax.annotation-api:${property("javaxVersion")}")
    implementation("org.projectlombok:lombok:1.18.26")

    /* Servlet */
    compileOnly("javax.servlet:javax.servlet-api:4.0.1")

    /* Json */
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("io.jsonwebtoken:jjwt:0.9.1")

    /* Databases */
    implementation("org.postgresql:postgresql:42.6.0")
    implementation("org.liquibase:liquibase-core:4.21.1")
    compileOnly("org.redisson:redisson-spring-boot-starter:3.20.1")

    /* Lombok */
    annotationProcessor("org.projectlombok:lombok:1.18.26")
    testCompileOnly("org.projectlombok:lombok:1.18.26")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.26")

    /* Api */
    implementation("org.openapitools:jackson-databind-nullable:${property("openapitoolsVersion")}")
    implementation("org.springdoc:springdoc-openapi-ui:${property("openapiUiVersion")}")
    compileOnly("org.openapitools:openapi-generator-gradle-plugin:${property("openapiVersion")}")

    /* --------- Tests --------- */
    /* JUnit */
    testImplementation("org.junit.platform:junit-platform-runner:1.9.2")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.2")

    /* Spring */
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.0.5")
    testImplementation("org.springframework.security:spring-security-test:6.0.3")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<JavaCompile> {
    dependsOn(tasks.clean)
    dependsOn(tasks.openApiGenerate)
    options.encoding = "UTF-8"
    source("$buildDir/generated/src/main/kotlin")
}

sourceSets {
    main {
        java {
            srcDir("${buildDir.absolutePath}/generated/src/main/java")
        }
    }
}

openApiGenerate {
    generatorName.set("spring")
    inputSpec.set("$rootDir/api-spec/tower-defense-api.yaml")
    outputDir.set("$buildDir/generated")
    apiPackage.set("ru.nsu.shelbogashev.tdgserver.generated.api")
    modelPackage.set("ru.nsu.shelbogashev.tdgserver.generated.api.dto")
    packageName.set("ru.nsu.shelbogashev.tdgserver.generated")
    configOptions.set(
        mapOf(
            "useTags" to "true",
            "delegatePattern" to "true"
        )
    )
}