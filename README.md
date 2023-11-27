# Charts
Electronic Navigational Charts Software

## Building with a JVM

The project uses gradle as it's build system.

I use Gradle 8.3 installed via the wonderful [sdkman](https://sdkman.io/).

1. Check the code out.
2. Load it in your favorite IDE.
3. Do whatever you do there for gradle builds.

## Building A Native Image

The gradle build is configured to allow native image builds using Graal.

To build a native image for platform X do:
```
gradle -Ptarget=X clean nativeCompile nativeLink nativePackage nativeInstall nativeRun
```
(X = host, ios, ios-sim, android)

I have tested it on MacOS building for host, ios and ios-sim. I haven't tested on Android.


