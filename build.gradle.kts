tasks {
    getByName<Wrapper>("wrapper") {
        gradleVersion = "5.3.1"
        distributionType = Wrapper.DistributionType.ALL
    }
}

defaultTasks(
    ":sdk:clean", ":sdk:build",
    ":decoder:clean", ":decoder:build"
)

allprojects {
    repositories {
        mavenCentral()
        jcenter()
        maven(url = "https://plugins.gradle.org/m2/")
    }
}
