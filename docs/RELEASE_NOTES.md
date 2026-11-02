# Release title

VulkanMod: Flashback Edition — Minecraft 1.21.10 · Experimental 1

# Tag

v0.5.7-flashback.5-rollback.1-experimental

# Release body (copy below)

Experimental VulkanMod fork by **MrIniuserYT**, adding a Flashback compatibility
bridge for **Minecraft 1.21.10 / Fabric / Java 21**.

## Included
- Main-window Flashback editor rendering bridge.
- Synchronous export frame readback and Vulkan frame-lifecycle handling.
- Custom fork branding.
- Rollback to the export path the owner reported working in their setup.

## Install
Install Fabric API and Flashback 0.39.10 separately from their authorized sources.
Download the normal mod JAR below and place it in `mods`. Remove the original
VulkanMod and previous fork JARs; all use the `vulkanmod` mod ID.
Do not install the `-sources.jar` or GitHub's source ZIP as a mod.

## Known limitations
Export is synchronous and may be slow. The preview may be upside down even if
the final MP4 is correct. Detached editor windows are unsupported. Native output
resolution independent of the swapchain/SSAA above window resolution is not certified.
Audio sync, transparent exports, long exports, hardware combinations and other mods
need more testing.

**Prerelease — not a stable or 100%-compatible build. Back up worlds and replays.**
The owner reports successful export in their setup; no broad compatibility test
matrix or automated in-game test has been completed.

Credits: upstream VulkanMod by Collateral and contributors; Flashback by Moulberry
and contributors. This is an unofficial fork. Flashback is not bundled.

---

Maintainer checklist (do not paste into the release body):
- Mark this release as **Pre-release**.
- Build with JDK 21 and the included wrapper.
- Run the smoke tests in VALIDATION.md on the exact JAR being uploaded.
- Upload the remapped mod JAR and matching `-sources.jar`; include checksums if desired.
- Do not attach private development ZIPs, Flashback sources, local API JARs, logs or replays.
- Review licensing/publication rights before publishing any binary.
