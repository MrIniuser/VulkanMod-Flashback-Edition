# Local compile-time API (not redistributed)

This project imports `imgui.moulberry90`, the Flashback-specific ImGui Java API.
The binding is a **compileOnly** dependency: it is neither committed nor included
in the resulting mod JAR. Flashback supplies these classes at runtime.

Obtain `imgui-binding-1.90.0.jar` through a source you are authorized to access,
for example the `deps` directory of your existing Flashback source checkout.
Copy it to:

```text
local-deps/imgui-binding-1.90.0.jar
```

Do not substitute a normal `imgui-java` JAR: the package names/API differ.
Do not upload Flashback sources, this JAR, or its natives to a public repository
unless the rights holder has granted the necessary permission.

The private full project archive previously supplied by the developer contained
reference material for local development. It is not a public release package.

The API is required for `gradlew build`, but not for the standalone helper tests
or Java syntax parser. See docs/BUILDING.md for CI setup.
