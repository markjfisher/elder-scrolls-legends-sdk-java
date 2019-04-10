import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin(module = "jvm") version "1.3.21"
    kotlin(module = "kapt") version "1.3.21"
    kotlin(module = "plugin.allopen") version "1.3.21"
}

group = "elderscrolls"
version = "1.0.0"

val junitJupiterEngineVersion: String by project
val jacksonVersion: String by project
val logbackClassicVersion: String by project
val assertJVersion: String by project
val mockkVersion: String by project
val logbackEncoderVersion: String by project
val jsonAssertVersion: String by project
val unirestJavaVersion: String by project
val unirestJacksonVersion: String by project
val konfigVersion: String by project

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("com.konghq:unirest-java:$unirestJavaVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    implementation("ch.qos.logback:logback-classic:$logbackClassicVersion")
    implementation("com.natpryce:konfig:$konfigVersion")

    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitJupiterEngineVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitJupiterEngineVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:$junitJupiterEngineVersion")
    testImplementation("org.assertj:assertj-core:$assertJVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("org.skyscreamer:jsonassert:$jsonAssertVersion")

}

tasks {
    getByName<Wrapper>("wrapper") {
        gradleVersion = "5.3.1"
        distributionType = Wrapper.DistributionType.ALL
    }

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

allprojects {
    repositories {
        mavenCentral()
        jcenter()
        maven(url = "https://plugins.gradle.org/m2/")
    }
}
