# Release processes

## Tagging

```bash
git tag -a v0.X.Y <commit hash> -m "Version 0.X.Y"
git push origin v0.X.Y 
```

## Teamcity

Trigger a new deployment in [Publish release](https://teamcity.jetbrains.com/buildConfiguration/JetBrainsPublicProjects_Skija_Skiko_PublishRelease)
build configuration.
    1. Click "Deploy" button.
    2. Specify the desired version in "Skiko Release Version" text field on the "Parameters" tab.
    3. Choose the desired branch and commit on the "Changes" tab.
    4. Optionally you can check "Put the build to the queue top" option in the "General" tab to speed up a deployment
    (please be mindful about it!).

## Publishing

##### Publish JVM target to Maven local
```
./gradlew publishToMavenLocal
```

##### Publish all targets to Maven Local
```
./gradlew publishToMavenLocal -Pskiko.native.enabled=true -Pskiko.wasm.enabled=true -Pskiko.android.enabled=true
```

##### Publish to `build/repo` directory
```
./gradlew publishToBuildRepo
```

##### Publish to Compose repo
Set up environment variables `COMPOSE_REPO_USERNAME` and `COMPOSE_REPO_KEY`.
```
./gradlew publishToComposeRepo
```

##### Publish to all repositories
```
./gradlew publish
```

##### Publish local version
```
./gradlew <PUBLISH_TASK> -Pdeploy.version=0.2.0 -Pdeploy.release=true
```

##### Code signing

macOS for Apple Silicon builds aimed for distribution require mandatory code signing,
so use command like
```
./gradlew -Psigner="Apple Distribution: Nikolay Igotti (N462MKSJ7M)" <PUBLISH_TASK>
```
to codesign the JNI library.
Use `security find-identity -v -p codesigning` to find valid signing identities.
