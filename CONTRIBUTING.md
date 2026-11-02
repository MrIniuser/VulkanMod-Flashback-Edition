# Contributing

Keep renderer/export changes narrow and reproducible. Distinguish preview output
from saved frames and document changes to frame lifecycle or Vulkan synchronization.

Use JDK 21. Run the standalone checks and a real game smoke test; source parsing
alone cannot validate Minecraft APIs, Mixin injection or native Vulkan behavior.

Do not commit Flashback sources, local API binaries, game files, logs with tokens,
private replays, or copied third-party assets without suitable permission.
Document your OS/GPU/driver and export settings when reporting test results.

Preserve upstream attribution and existing licenses. Never label unverified
compatibility as guaranteed. Discuss larger async-readback changes before combining
them with unrelated preview/UI work.
