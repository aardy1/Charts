# Knowtiphy Charts

[to contact me see the gist on my profile]

The project has two goals:

1. build a pure JavaFX ESRI shape file renderer (so no use of AWT or bridges to AWT)
2. build an Electronic Navigation Chart (ENC) viewer (inspired by or plugging into [OpenCPN](https://opencpn.org)) running natively on my iPhone (and generaly all desktop and mobile platforms)

Part 1 enables part 2, because JavaFX runs on all platforns, including mobile.

The project has basic stuff working -- some screen recordings of it running on my iPhone with the US_REGION08 ENC charts can be found [here](https://github.com/aardy1/Charts/wiki/Videos-of-Knowtiphy-Charts-running-on-an-iPhone) (sometimes Github says it can't show the videos -- it can, just reload the page, possibly more than once).

## Building and Running Knowtiphy Charts with a JVM on the Command Line for a Desktop

You need three things:
1. Gradle
2. a JVM (you might need one with JavaFX linked in? -- see below).
3. some charts

**Note**: on a Mac you must have a JVM built for ARM.

I use Gradle 8.3 and java 22.1.0.1.r17-gln (a gluon build), both installed via the wonderful [sdkman](https://sdkman.io/).

There are two charts (the ones used in the videos) [here](https://github.com/aardy1/Charts/files/13483291/ENC.zip). You need to unzip the charts file into `HOME/Documents/Knowtipy Charts`, where `HOME` is your home directory, so you will have `HOME/Documents/Knowtipy Charts/ENC/US_REGION08/*`.

To build and run:

1. Check the code out
2. Open a terminal, go to the root directory of the project and type `gradle run`

This will cause Gradle to download all the libraries (including the OpenJFX libraries) it needs, and compile and run Knowtiphy Charts. I have tested this on a Mac and on Ubuntu (running under QEMU).

**Note**: due to a known bug in recent versions of the JVM, it is possible that Knowtiphy Charts opens "in the background" (so you can't see it). Use whatever is right for your OS to see all running apps (on my Mac I use a three finger swipe up).

## Building a Native Image for Desktop

The gradle build is configured for native image builds using Graal.

To get the build to work you will need some things:

1. Gradle
2. Graal -- get it from Gluon or use the 22.1.0.1.r17-gln mentioned above via sdkman
3. set `GRAALVM_HOME` environment variable to the parent of the bin directory in 1. So for me that is `export GRAALVM_HOME=/Users/graham/.sdkman/candidates/java/22.1.0.1.r17-gln`.

In theory that should be it :-)

To build and run a native image, go the project root dir, and do:
```
./gradlew -Ptarget=host nativeBuild
```
The native build will take a while and the binary image will be installed under `X/build/gluonfx/A` where X is the project root, and A is your architecture and operating system (e.g. aarch64-darwin).

I have tested this process on MacOS X.

## Running Knowtiphy Charts Native on a Desktop

To run the app as a native image do:
```
gradle -Ptarget=host nativeBuild nativeRun
```
Or just `gradle -Ptarget=host nativeRun` if you have already built it, or just double click the native image that was previously built.

You will also need some charts.

## Building Knowtiphy Charts with your favorite IDE

Good luck :-)

This bit is usually a pain in the neck and is different for every platform and IDE. There are some instructions [here](https://openjfx.io/openjfx-docs/#introduction).

For Apache Netbeans (with UI build actions delegated to gradle), you shouldn't have to do very much. You can just click the run button and the app should run on the JVM.

For IntelliJ I followed the [instructions](https://openjfx.io/openjfx-docs/#IDE-Intellij) for "Non-modular projects" -- except Step 1 of course since you already have a project! 

After Step 3. I could run Knowtiphy Charts from the Gradle window in Intellij.


## Running Knowtiphy Charts Native on a Phone or Tablet

To build a native image for iOS you must be on a Mac.

To build a native image for Android you must be running Linux.

You build the native image as above `gradle -Ptarget=X nativeCompile ...`, with `X = ios` or `X = android`.

Now comes the hacky bit :-) 

Instructions later ...

The `nativeRun`gradle task install the app on your phone ... to do ...

You need a chart on your phone. On iOS download the shape file example above to 




