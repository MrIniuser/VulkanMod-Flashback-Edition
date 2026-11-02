"""Static regression guard; not a substitute for Minecraft startup testing."""
from pathlib import Path
import re
root = Path(__file__).resolve().parents[1]
source = root / "src/main/java/net/vulkanmod/render/shader/CustomRenderPipelines.java"
text = source.read_text(encoding="utf-8")
invalid = re.findall(r'\.(?:withLocation|withVertexShader|withFragmentShader)\("[^"\n]*:[^"\n]*"\)', text)
assert not invalid, invalid
ids = re.findall(r'ResourceLocation.fromNamespaceAndPath\("([^"]+)", "([^"]+)"\)', text)
assert len(ids) == 6, ids
for namespace, path in ids:
    assert re.fullmatch(r"[a-z0-9_.-]+", namespace)
    assert re.fullmatch(r"[a-z0-9/._-]+", path)
for extension in ("vsh", "fsh"):
    assert (root / f"src/main/resources/assets/vulkanmod/shaders/core/flashback_ui.{extension}").is_file()
print("PASS: 6 explicitly namespaced resource identifiers and 2 shader files. Static check only.")
