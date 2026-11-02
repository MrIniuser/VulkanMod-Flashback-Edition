# Changelog

## 0.5.7-flashback.5-rollback.1-experimental — Experimental 1

### Added
- Flashback main-window editor rendering integration for the target Minecraft 1.21.10 setup.
- Synchronous frame readback and export frame-lifecycle adaptation.
- VulkanMod: Flashback Edition branding supplied by the project owner.
- Source build instructions and limited automated checks.

### Fixed during internal testing
- Invalid namespaced pipeline/shader registration.
- Missing Vulkan frame initialization in Flashback's separate export loop.
- Unsupported progress presentation path.

### Reverted
- The later preview/readback optimization attempt was rolled back after a user-reported regression.
- Runtime Java source matches internal revision .3; user icons are retained.

### Known issues
- Slow synchronous export; preview can be upside down.
- Detached editor windows unsupported; native independent high-resolution main pass not implemented.
- Limited hardware coverage. No stable or universal compatibility claim.
