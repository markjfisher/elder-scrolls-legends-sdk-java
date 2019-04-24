import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin(module = "jvm") version "1.3.30"
    application
}

val junitJupiterEngineVersion: String by project
val jacksonVersion: String by project
val assertJVersion: String by project
val mockkVersion: String by project
val unirestJavaVersion: String by project
val konfigVersion: String by project
val jsoupVersion: String by project
val cacheVersion: String by project

dependencies {
    implementation(project(":sdk"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.apache.httpcomponents:httpclient-cache:$cacheVersion")
    implementation("com.konghq:unirest-java:$unirestJavaVersion") {
        exclude(group = "org.apache.httpcomponents", module = "httpclient-cache")
    }
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    implementation("com.natpryce:konfig:$konfigVersion")
    implementation("org.jsoup:jsoup:$jsoupVersion")

    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitJupiterEngineVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitJupiterEngineVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:$junitJupiterEngineVersion")
    testImplementation("org.assertj:assertj-core:$assertJVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")
}

tasks {
    named<KotlinCompile>("compileKotlin") {
        kotlinOptions {
            jvmTarget = "1.8"
            javaParameters = true
        }
    }

    named<KotlinCompile>("compileTestKotlin") {
        kotlinOptions {
            jvmTarget = "1.8"
            javaParameters = true
        }
    }

    withType<Test> {
        maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).takeIf { it > 0 } ?: 1
    }

    named<Test>("test") {
        useJUnitPlatform()
    }

}

defaultTasks(
    "clean", "build"
)

application {
    mainClassName = "io.elderscrollslegends.CodeMapper"
}