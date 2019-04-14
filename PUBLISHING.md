I followed the guides from:

- [Gradle maven-publish docs](https://docs.gradle.org/current/userguide/publishing_maven.html#publishing_maven:complete_example)
- [Sign and Publish On Maven Central Using maven-publish In Gradle](https://medium.com/@nmauti/sign-and-publish-on-maven-central-a-project-with-the-new-maven-publish-gradle-plugin-22a72a4bfd4b)

# Publishing to maven

To test your output, you can first publish into local folder sdk/build/repos with

    ./gradlew :sdk:publishMavenJavaPublicationToMavenRepository

To publish to local maven .m2 dir

    ./gradlew :sdk:publishToMavenLocal

To publish to maven

    ./gradlew -Plive :sdk:publishMavenJavaPublicationToMavenRepository

You must have settings as follows on your path (typically ~/.gradle/gradle.properties):

    signing.keyId=<GPG SHORT 8 BYTE KEY ID - SEE BELOW>
    signing.password=<PASSWORD FOR GPG KEY>
    signing.secretKeyRingFile=<PATH TO SECRING.GPG FILE>
    
    sonatypeUsername=<SONATYPE USER NAME FROM JIRA TO GET GROUP ID>
    sonatypePassword=<PASSWORD TO SONATYPE ACCOUNT>

## GPG bits

### Creating secring.gpg

On the mac, the secring file didn't exist, as they are now kept in sub-folders.

To work around this I ran:

```bash
gpg --export-secret-keys -o ~/.gnupg/secring.gpg
```

### Finding the key id

To get the `GPG SHORT 8 BYTE KEY ID`, use following:

```text
$ gpg -k --keyid-format short
/path/to/.gnupg/pubring.kbx
-------------------------------------
pub   rsa2048/AABBCCDD 2019-01-01 [SC] [expires: 2021-01-01]
      26B113F527568433F50E33A395D92A526468D3B3
...
```
In the above, the key is AABBCCDD
