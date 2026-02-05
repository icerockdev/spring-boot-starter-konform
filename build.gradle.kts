@file:Suppress("ObjectLiteralToLambda")

import java.util.Base64
import org.jreleaser.gradle.plugin.dsl.deploy.maven.MavenCentralMavenDeployer
import org.jreleaser.model.Active
import org.gradle.api.Action

plugins {
    kotlin("jvm") version "2.0.21"
    kotlin("kapt") version "2.0.21"
    id("maven-publish")
    id("java-library")
    id("signing")
    id("org.jreleaser") version "1.18.0"
}

group = "com.icerockdev.boko"
version = "0.1.2"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("reflect"))
    api("io.konform:konform:0.11.1")
    implementation("org.springframework.boot:spring-boot-autoconfigure:3.1.12")
    implementation("org.springframework.boot:spring-boot-starter-aop:3.1.12")

    testImplementation(kotlin("test"))
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:2.0.21")
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

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
    withJavadocJar()
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

val publishRepositoryName = "maven-central-portal-deploy"
publishing {
    repositories.maven(layout.buildDirectory.dir(publishRepositoryName))
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
            setRequired({ !properties.containsKey("libraryPublishToMavenLocal") })
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

jreleaser {
    gitRootSearch = true
    release {
        generic {
            skipRelease = true
            skipTag = true
            changelog {
                enabled = false
            }
            token = "EMPTY"
        }
    }
    deploy {
        maven {
            mavenCentral.create("sonatype", object : Action<MavenCentralMavenDeployer> {
                override fun execute(t: MavenCentralMavenDeployer) = t.run {
                    enabled = !properties.containsKey("libraryPublishToMavenLocal")
                    applyMavenCentralRules = true
                    sign = false
                    active = Active.ALWAYS
                    url = "https://central.sonatype.com/api/v1/publisher"
                    stagingRepository(layout.buildDirectory.dir(publishRepositoryName).get().toString())
                    setAuthorization("Basic")
                    retryDelay = 60
                    username = System.getenv("OSSRH_USER")
                    password = System.getenv("OSSRH_KEY")
                }
            })
        }
    }
}
