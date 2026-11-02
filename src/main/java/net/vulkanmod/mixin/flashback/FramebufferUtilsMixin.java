// SPDX-License-Identifier: LGPL-3.0-only
package net.vulkanmod.mixin.flashback;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import net.vulkanmod.compat.flashback.FlashbackBlitter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "com.moulberry.flashback.FramebufferUtils", remap = false)
public abstract class FramebufferUtilsMixin {
    @Inject(method = "clear", at = @At("HEAD"), cancellable = true)
    private static void vmfb$clear(RenderTarget target, int color, CallbackInfo ci) {
        var encoder = RenderSystem.getDevice().createCommandEncoder();
        var c = target.getColorTexture();
        var d = target.getDepthTexture();
        boolean hasColor = c != null && !c.isClosed();
        boolean hasDepth = d != null && !d.isClosed();
        if (hasColor && hasDepth) encoder.clearColorAndDepthTextures(c, color, d, 1.0);
        else if (hasColor) encoder.clearColorTexture(c, color);
        else if (hasDepth) encoder.clearDepthTexture(d, 1.0);
        ci.cancel();
    }
    @Inject(method = "blitToScreenPartial", at = @At("HEAD"), cancellable = true)
    private static void vmfb$partial(RenderTarget source, int width, int height,
                                    float x1, float y1, float x2, float y2, CallbackInfo ci) {
        FlashbackBlitter.partialToMain(source, width, height, x1, y1, x2, y2);
        ci.cancel();
    }
}
