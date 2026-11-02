// SPDX-License-Identifier: LGPL-3.0-only
package net.vulkanmod.mixin.flashback;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import net.minecraft.client.Minecraft;
import net.vulkanmod.compat.flashback.FlashbackBlitter;
import net.vulkanmod.vulkan.Renderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Flashback's export loop bypasses Minecraft.runTick's normal Vulkan frame hooks. */
@Pseudo
@Mixin(targets = "com.moulberry.flashback.exporting.ExportJob", remap = false)
public abstract class ExportJobMixin {
    @Unique
    private static void vmfb$ensureFrame() {
        RenderSystem.assertOnRenderThread();
        if (Renderer.isRecording()) return;
        Renderer renderer = Renderer.getInstance();
        renderer.preInitFrame();
        renderer.beginFrame();
        if (!Renderer.isRecording()) {
            throw new IllegalStateException("Flashback export could not begin a Vulkan frame. Restore the Minecraft window and retry export.");
        }
    }

    @WrapOperation(method = "doExport", at = @At(value = "INVOKE",
            target = "Lcom/mojang/blaze3d/systems/CommandEncoder;clearColorAndDepthTextures(Lcom/mojang/blaze3d/textures/GpuTexture;ILcom/mojang/blaze3d/textures/GpuTexture;D)V",
            remap = false), require = 2)
    private void vmfb$beginExportFrame(CommandEncoder encoder, GpuTexture oldColor,
                                      int color, GpuTexture oldDepth, double depth, Operation<Void> original) {
        vmfb$ensureFrame();
        // beginFrame can acquire a different swapchain image or resize the swapchain.
        // The original call's texture arguments were evaluated BEFORE acquisition.
        // Re-fetch both attachments instead of using stale handles.
        RenderTarget main = Minecraft.getInstance().getMainRenderTarget();
        original.call(encoder, main.getColorTexture(), color, main.getDepthTexture(), depth);
    }

    @Inject(method = "finishFrame", at = @At("HEAD"))
    private void vmfb$beginProgressFrame(CallbackInfoReturnable<Boolean> cir) {
        // Finalization callbacks may redraw after the previous window.updateDisplay
        // has ended/submitted the Vulkan command buffer.
        vmfb$ensureFrame();
    }

    @WrapOperation(method = "finishFrame", at = @At(value = "INVOKE",
            target = "Lcom/mojang/blaze3d/pipeline/RenderTarget;blitToScreen()V", remap = false), require = 1)
    private void vmfb$presentProgress(RenderTarget display, Operation<Void> original) {
        vmfb$ensureFrame();
        // VkCommandEncoder.presentTexture is not implemented upstream. Compose the
        // progress image into the active swapchain; updateDisplay still submits it
        // through VulkanMod's existing RenderSystem.flipFrame hook.
        FlashbackBlitter.copy(display.getColorTextureView(),
                Minecraft.getInstance().getMainRenderTarget().getColorTextureView(), 0, 0, 1, 1, false);
    }
}
