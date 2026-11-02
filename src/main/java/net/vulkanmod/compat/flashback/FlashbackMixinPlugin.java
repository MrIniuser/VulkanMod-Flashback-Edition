// SPDX-License-Identifier: LGPL-3.0-only
package net.vulkanmod.compat.flashback;

import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import java.util.List;
import java.util.Set;

/** Does not load Flashback or ImGui classes when Flashback is absent. */
public final class FlashbackMixinPlugin implements IMixinConfigPlugin {
    private boolean enabled;
    public void onLoad(String pkg) { enabled = FabricLoader.getInstance().isModLoaded("flashback"); }
    public boolean shouldApplyMixin(String target, String mixin) { return enabled; }
    public String getRefMapperConfig() { return null; }
    public List<String> getMixins() { return null; }
    public void acceptTargets(Set<String> own, Set<String> other) {}
    public void preApply(String name, ClassNode node, String mixin, IMixinInfo info) {}
    public void postApply(String name, ClassNode node, String mixin, IMixinInfo info) {}
}
