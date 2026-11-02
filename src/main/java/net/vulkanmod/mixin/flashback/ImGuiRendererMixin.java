// SPDX-License-Identifier: LGPL-3.0-only
package net.vulkanmod.mixin.flashback;

import imgui.moulberry90.ImDrawData;
import net.vulkanmod.compat.flashback.FlashbackImGuiRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "com.moulberry.flashback.editor.ui.CustomImGuiImplGl3", remap = false)
public abstract class ImGuiRendererMixin {
    @Unique private FlashbackImGuiRenderer vmfb$renderer;
    @Unique private FlashbackImGuiRenderer vmfb$renderer() {
        if (vmfb$renderer == null) vmfb$renderer = new FlashbackImGuiRenderer();
        return vmfb$renderer;
    }
    @Inject(method = "init(Ljava/lang/String;)Z", at = @At("HEAD"), cancellable = true)
    private void vmfb$init(String glsl, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(vmfb$renderer().init());
    }
    @Inject(method = "newFrame", at = @At("HEAD"), cancellable = true)
    private void vmfb$newFrame(CallbackInfo ci) { vmfb$renderer().newFrame(); ci.cancel(); }
    @Inject(method = "renderDrawData", at = @At("HEAD"), cancellable = true)
    private void vmfb$render(ImDrawData data, CallbackInfo ci) { vmfb$renderer().render(data); ci.cancel(); }
    @Inject(method = "updateFontsTexture", at = @At("HEAD"), cancellable = true)
    private void vmfb$fonts(CallbackInfo ci) { vmfb$renderer().updateFonts(); ci.cancel(); }
    @Inject(method = "destroyFontsTexture", at = @At("HEAD"), cancellable = true)
    private void vmfb$destroyFonts(CallbackInfo ci) { vmfb$renderer().destroyFonts(); ci.cancel(); }
    @Inject(method = "destroyDeviceObjects", at = @At("HEAD"), cancellable = true)
    private void vmfb$destroy(CallbackInfo ci) { vmfb$renderer().destroyFonts(); ci.cancel(); }
    @Inject(method = "shutdown", at = @At("HEAD"), cancellable = true)
    private void vmfb$shutdown(CallbackInfo ci) { vmfb$renderer().close(); ci.cancel(); }
}
