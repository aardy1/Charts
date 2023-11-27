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

To get the build to work you will need a couple of things:

1. Graal -- get it from [Gluon](https://github.com/gluonhq/graal/releases/tag/gluon-22.1.0.1-Final)
2. set `GRAALVM_HOME` environment variable to the parent of the bin directory in 1. So for me that is `export GRAALVM_HOME=/Users/graham/Downloads/graalvm-svm-java17-darwin-m1-gluon-22.1.0.1-Final/Contents/Home`

In theory that should be it :-)

To build a native image for platform X do:
```
gradle -Ptarget=X clean nativeCompile nativeLink nativePackage nativeInstall nativeRun
```
(X = host (host = the platform you are running gradle on), ios, ios-sim, android)

I have tested it on MacOS X building for host, ios and ios-sim. I haven't tested on Android.

## Running the App (using JVM or native on a desktop)

To run the app you will need some charts, which must be ESRI shape files.

Shape files are big, too big to attach here. Email me :-)

You need to unzip the file, and put it in `HOME/Documents/Knowtipy Charts`, where `HOME` is your home directory.




