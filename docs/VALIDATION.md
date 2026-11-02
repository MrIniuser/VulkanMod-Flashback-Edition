# Validation status

## Evidence available

- Project owner reported that the rollback export path appears to work in their setup.
- 305 runtime Java source files match internal revision .3 byte-for-byte.
- Original runtime resources match .3 except the two supplied branding icons.
- Local CPU helper tests: 20 checks passed.
- Java 21 syntax parser: 305 source files, zero syntax errors.
- Static shader identifier/resource checks passed.

## Not established

No independent full compilation, in-game launch, GPU validation run, performance
benchmark, or cross-platform compatibility matrix was completed by the preparer.
The sanitized public build layout (local-deps instead of private reference/) must
also be built and tested by the maintainer before attaching binaries.

## Before each release

- [ ] Build on JDK 21 with the exact release tag and authorized local API.
- [ ] Start with and without Flashback; verify no missing optional classes.
- [ ] Record, save, reopen, seek, and export a short replay.
- [ ] Verify MP4 orientation, colors, duration, audio sync and file completion.
- [ ] Check cancellation, repeated exports and memory usage.
- [ ] Test preview separately from the saved output.
- [ ] Record the GPU, driver, OS and exact mod versions tested.
- [ ] Confirm no API JAR/Flashback classes or sources were bundled accidentally.
- [ ] Confirm public logs/artifacts contain no tokens or private recordings.
- [ ] Review code, asset and dependency licenses; mark release as prerelease.
