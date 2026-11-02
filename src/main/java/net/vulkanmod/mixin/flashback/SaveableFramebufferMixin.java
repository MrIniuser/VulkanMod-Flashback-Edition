// SPDX-License-Identifier: LGPL-3.0-only
package net.vulkanmod.mixin.flashback;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.textures.GpuTexture;
import net.vulkanmod.compat.flashback.FrameReadback;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "com.moulberry.flashback.exporting.SaveableFramebuffer", remap = false)
public abstract class SaveableFramebufferMixin {
    @Unique private FrameReadback vmfb$readback;
    @Unique private FrameReadback vmfb$readback() {
        if (vmfb$readback == null) vmfb$readback = new FrameReadback();
        return vmfb$readback;
    }
    @Inject(method = "startDownload", at = @At("HEAD"), cancellable = true)
    private void vmfb$start(GpuTexture texture, int width, int height, CallbackInfo ci) {
        vmfb$readback().start(texture, width, height);
        ci.cancel();
    }
    @Inject(method = "finishDownload", at = @At("HEAD"), cancellable = true)
    private void vmfb$finish(int width, int height, CallbackInfoReturnable<NativeImage> cir) {
        cir.setReturnValue(vmfb$readback().finish(width, height));
    }
    @Inject(method = "close", at = @At("HEAD"), cancellable = true)
    private void vmfb$close(CallbackInfo ci) {
        vmfb$readback().close();
        ci.cancel();
    }
}
