// SPDX-License-Identifier: LGPL-3.0-only
package net.vulkanmod.mixin.flashback;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTextureView;
import net.vulkanmod.compat.flashback.FlashbackBlitter;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "com.moulberry.flashback.exporting.SaveableFramebufferQueue", remap = false)
public abstract class SaveableFramebufferQueueMixin {
    @Shadow @Final private GpuTextureView flipBufferView;
    @Inject(method = "blitFlip", at = @At("HEAD"), cancellable = true)
    private void vmfb$copy(RenderTarget source, boolean supersampling, CallbackInfo ci) {
        FilterMode old = source.filterMode;
        try {
            if (supersampling) source.setFilterMode(FilterMode.LINEAR);
            FlashbackBlitter.copy(source.getColorTextureView(), flipBufferView, 0, 0, 1, 1, false);
        } finally {
            if (supersampling) source.setFilterMode(old);
        }
        ci.cancel();
    }
}
