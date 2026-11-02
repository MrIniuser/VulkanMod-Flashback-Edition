# Building

## Local

1. Install a full JDK 21. `java -version` should report Java 21.
2. Place an authorized Flashback-specific ImGui API at
   `local-deps/imgui-binding-1.90.0.jar` (see that directory's README).
3. Use the wrapper, **not** an installed Gradle 8.10:

```bat
gradlew.bat clean build
```

Unix: `chmod +x gradlew && ./gradlew clean build`.
Internet access to Gradle, Fabric, Mojang and Maven dependencies is required.
Output is in `build/libs`. No game files or JDK are vendored in this repository.
Loom remains the upstream `1.12-SNAPSHOT`; dependency reproducibility is not yet locked.

## Standalone checks

These do not require the ImGui API:

```sh
sh scripts/test-core.sh
java tests/ParseJava.java src/main/java
python3 tests/check_resource_ids.py
```

Windows helper test: `scripts\test-core.bat`.
The parser only checks syntax, not types, API linkage or Mixin application.

## GitHub Actions

`checks.yml` runs standalone checks on pushes and pull requests. It is not a full mod build.

The public repository intentionally does not have an unattended full-build workflow:
the supplied ImGui API JAR is not authorized for public redistribution and its size
also exceeds GitHub's per-secret size limit when base64-encoded.

Build locally using your authorized dependency, then upload the tested mod JAR
and corresponding sources to a prerelease. A future CI build should fetch the API
from an authorized private dependency store with appropriately scoped credentials;
no such store or credentials are assumed or configured here.
