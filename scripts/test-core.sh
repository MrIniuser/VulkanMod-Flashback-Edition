#!/usr/bin/env sh
set -eu
cd "$(dirname "$0")/.."
mkdir -p build/core-tests
java -m jdk.compiler/com.sun.tools.javac.Main --release 21 -d build/core-tests   src/main/java/net/vulkanmod/compat/flashback/FramePixels.java   src/main/java/net/vulkanmod/compat/flashback/UiGeometry.java tests/CoreSelfTest.java
java -cp build/core-tests CoreSelfTest
