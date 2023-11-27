# Knowtiphy Charts

This project has two parts:

1. a "clone" of [OpenCPN](https://opencpn.org) running natively on all desktop and mobile platforms
2. a pure JavaFX ESRI shape file renderer (so no use of AWT or bridges to AWT)

The code runs just fine, but needs a lot of work of course.

## Building with a JVM

The project uses gradle as it's build system.

I use Gradle 8.3 installed via the wonderful [sdkman](https://sdkman.io/).

1. Check the code out.
2. Load it in your favorite IDE.
3. Do whatever you do there for gradle builds.

## Running the App

To run the app you will need some charts, which must be ESRI shape files.

A quite simple charts file is:

[ENC.zip](https://github.com/aardy1/Charts/files/13471740/ENC.zip)

## Running the App using a JVM

To run using a JVM, you first need to unzip the charts file into `HOME/Documents/Knowtipy Charts`, where `HOME` is your home directory.

You should then be able to run the app out of your IDE.

## Building a Native Image

The gradle build is configured to allow native image builds using Graal.

To get the build to work you will need a couple of things:

1. Graal -- get it from [Gluon](https://github.com/gluonhq/graal/releases/tag/gluon-22.1.0.1-Final)
2. set `GRAALVM_HOME` environment variable to the parent of the bin directory in 1. So for me that is `export GRAALVM_HOME=/Users/graham/Downloads/graalvm-svm-java17-darwin-m1-gluon-22.1.0.1-Final/Contents/Home`

In theory that should be it :-)

To build and run a native image for platform X do:
```
gradle -Ptarget=X nativeCompile nativeLink nativePackage nativeInstall
```
(X = host (host = the platform you are running gradle on), ios, ios-sim, android)

The native image will be installed under `X/build/gluonfx/A` where X is the project root, and A is your architecture and operating system (e.g. aarch64-darwin).

I have tested it on MacOS X building for host, ios and ios-sim.

## Running the App Native on a Desktop

To run the app as a native image for a desktop platform X do:
```
gradle -Ptarget=X nativeCompile nativeLink nativePackage nativeInstall nativeRun
```
(or just `gradle -Ptarget=X nativeRun` if you have already built it).

You will need some charts -- follow the steps for [running the app as a JVM build](https://github.com/aardy1/Charts/blob/main/README.md#running-the-app-using-a-jvm)

## Running the App on a Phone or Tablet

To build a native image for iOS you must be on a Mac running OS X.

To build a native image for Android you must be running Linux.

You build the native image as above `gradle -Ptarget=X nativeCompile ...`, with X = ios or X = android.

Now comes the hacky bit :-)

The `nativeRun`gradle task install the app on your phone ... to do ...

You need a chart on your phone. On iOS download the shape file example above to 




