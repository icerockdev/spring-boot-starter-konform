import java.util.Base64

plugins {
    kotlin("jvm") version "2.2.10"
    kotlin("kapt") version "2.2.10"
    id("maven-publish")
    id("java-library")
    id("signing")
}

group = "com.icerockdev.boko"
version = "0.1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("reflect"))
    api("io.konform:konform:0.11.1")
    implementation("org.springframework.boot:spring-boot-autoconfigure:3.1.12")
    implementation("org.springframework.boot:spring-boot-starter-aop:3.1.12")

    testImplementation(kotlin("test"))
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:2.2.10")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.5.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.5.0")
    testImplementation("org.springframework.boot:spring-boot-starter-web:3.1.12")
    testImplementation("org.springframework.boot:spring-boot-starter-tomcat:3.1.12")
    testImplementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.16.1")
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.1.12")
    testImplementation("org.springframework.boot:spring-boot-starter-validation:3.1.12")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

publishing {
    repositories.maven("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/") {
        name = "OSSRH"

        credentials {
            username = System.getenv("OSSRH_USER")
            password = System.getenv("OSSRH_KEY")
        }
    }
    publications {
        register("mavenJava", MavenPublication::class) {
            from(components["java"])
            artifact(sourcesJar.get())
            pom {
                name.set("Boko validations")
                description.set("Konform based validation for Kotlin/JVM")
                url.set("https://github.com/icerockdev/spring-boot-starter-konform")
                inceptionYear.set("2025")

                licenses {
                    license {
                        name.set("Apache License 2.0")
                        url.set("https://github.com/icerockdev/spring-boot-starter-konform/blob/master/LICENSE.md")
                    }
                }

                developers {
                    developer {
                        id.set("YokiToki")
                        name.set("Stanislav Karakovskii")
                        email.set("skarakovski@icerockdev.com")
                    }
                }

                scm {
                    connection.set("scm:git:ssh://github.com/icerockdev/spring-boot-starter-konform.git")
                    developerConnection.set("scm:git:ssh://github.com/icerockdev/spring-boot-starter-konform.git")
                    url.set("https://github.com/icerockdev/spring-boot-starter-konform")
                }
            }
        }

        signing {
            val signingKeyId: String? = System.getenv("SIGNING_KEY_ID")
            val signingPassword: String? = System.getenv("SIGNING_PASSWORD")
            val signingKey: String? = System.getenv("SIGNING_KEY")?.let { base64Key ->
                String(Base64.getDecoder().decode(base64Key))
            }
            useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)
            sign(publishing.publications["mavenJava"])
        }
    }
}
