import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin(module = "jvm") version "1.3.21"
    `java-library`
    `maven-publish`
    signing
}

group = "net.markjfisher"
version = "1.0.0"

val junitJupiterEngineVersion: String by project
val jacksonVersion: String by project
val assertJVersion: String by project
val mockkVersion: String by project
val unirestJavaVersion: String by project
val konfigVersion: String by project

val sonatypeUsername: String by project
val sonatypePassword: String by project

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.konghq:unirest-java:$unirestJavaVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    implementation("com.natpryce:konfig:$konfigVersion")

    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitJupiterEngineVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitJupiterEngineVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:$junitJupiterEngineVersion")
    testImplementation("org.assertj:assertj-core:$assertJVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")
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

    register<Jar>("sourcesJar") {
        from(sourceSets.main.get().allSource)
        archiveClassifier.set("sources")
    }

    register<Jar>("javadocJar") {
        from(javadoc)
        archiveClassifier.set("javadoc")
    }

    javadoc {
        if (JavaVersion.current().isJava9Compatible) {
            (options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifact(tasks["sourcesJar"])
            artifact(tasks["javadocJar"])
            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }
            pom {
                name.set("Elder Scrolls: Legends SDK - Java")
                description.set("A java wrapper around the Elder Scrolls: Legends API of https://elderscrollslegends.io")
                url.set("https://github.com/markjfisher/elder-scrolls-legends-sdk-java")
                licenses {
                    license {
                        name.set("The MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
                developers {
                    developer {
                        id.set("mark.j.fisher")
                        name.set("Mark Fisher")
                        email.set("mark.j.fisher@gmail.com")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/markjfisher/elder-scrolls-legends-sdk-java.git")
                    developerConnection.set("scm:git:ssh://github.com/markjfisher/elder-scrolls-legends-sdk-java.git")
                    url.set("https://github.com/markjfisher/elder-scrolls-legends-sdk-java")
                }
                issueManagement {
                    system.set("Github")
                    url.set("https://github.com/markjfisher/elder-scrolls-legends-sdk-java/issues")
                }
            }
        }
    }
    repositories {
        maven {
            // change URLs to point to your repos, e.g. http://my.org/repo
            var releasesRepoUrl = uri("$buildDir/repos/releases")
            var snapshotsRepoUrl = uri("$buildDir/repos/snapshots")
            if (project.hasProperty("live")) {
                releasesRepoUrl = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2")
                snapshotsRepoUrl = uri("https://oss.sonatype.org/content/repositories/snapshots")
                credentials.username = sonatypeUsername
                credentials.password = sonatypePassword
            }
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
        }
    }
}

signing {
    sign(publishing.publications["mavenJava"])
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
