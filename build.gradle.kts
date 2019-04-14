tasks {
    getByName<Wrapper>("wrapper") {
        gradleVersion = "5.3.1"
        distributionType = Wrapper.DistributionType.ALL
    }
}

defaultTasks(
    ":sdk:clean", ":sdk:build"
)
