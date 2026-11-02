@echo off
cd /d "%~dp0\.."
if not exist build\core-tests mkdir build\core-tests
java -m jdk.compiler/com.sun.tools.javac.Main --release 21 -d build/core-tests src/main/java/net/vulkanmod/compat/flashback/FramePixels.java src/main/java/net/vulkanmod/compat/flashback/UiGeometry.java tests/CoreSelfTest.java
if errorlevel 1 exit /b 1
java -cp build/core-tests CoreSelfTest
