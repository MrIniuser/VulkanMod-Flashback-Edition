// SPDX-License-Identifier: LGPL-3.0-only
package net.vulkanmod.compat.flashback;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.Minecraft;
import net.vulkanmod.render.shader.CustomRenderPipelines;
import net.vulkanmod.render.engine.VkGpuTexture;
import net.vulkanmod.vulkan.Renderer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import java.nio.ByteBuffer;
import java.util.OptionalInt;

/** Texture-space copies are top-down on Vulkan; never use Flashback's OpenGL Y-flip. */
public final class FlashbackBlitter {
    private FlashbackBlitter() {}

    public static void copy(GpuTextureView source, GpuTextureView target,
                            float x1, float y1, float x2, float y2, boolean clear) {
        if (source.texture() == target.texture())
            throw new IllegalArgumentException("Cannot sample an active color attachment");
        // Transition on the same graphics command buffer, after ending the producer.
        // Letting descriptor binding schedule this through the upload queue can place
        // the barrier before the frame which actually produced the source image.
        Renderer.getInstance().endRenderPass();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            ((VkGpuTexture)source.texture()).getVulkanImage().readOnlyLayout(stack, Renderer.getCommandBuffer());
        }
        ByteBuffer bytes = MemoryUtil.memAlloc(6 * DefaultVertexFormat.POSITION_TEX_COLOR.getVertexSize());
        try {
            vertex(bytes, x1, y1, 0, 0); vertex(bytes, x1, y2, 0, 1); vertex(bytes, x2, y2, 1, 1);
            vertex(bytes, x1, y1, 0, 0); vertex(bytes, x2, y2, 1, 1); vertex(bytes, x2, y1, 1, 0);
            bytes.flip();
            try (GpuBuffer vertices = RenderSystem.getDevice().createBuffer(
                    () -> "Flashback blit vertices", GpuBuffer.USAGE_VERTEX | GpuBuffer.USAGE_COPY_DST, bytes);
                 var pass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(
                    () -> "Flashback Vulkan blit", target, clear ? OptionalInt.of(0) : OptionalInt.empty())) {
                pass.setPipeline(CustomRenderPipelines.FLASHBACK_COPY);
                pass.bindSampler("Sampler0", source);
                pass.setVertexBuffer(0, vertices);
                pass.draw(0, 6);
            }
        } finally { MemoryUtil.memFree(bytes); }
    }
    private static void vertex(ByteBuffer buffer, float x, float y, float u, float v) {
        buffer.putFloat(x * 2 - 1).putFloat(1 - y * 2).putFloat(0);
        buffer.putFloat(u).putFloat(v);
        buffer.putInt(-1);
    }
    public static void partialToMain(RenderTarget source, int width, int height,
                                     float x1, float y1, float x2, float y2) {
        // Snapshot first: VulkanMod normally renders directly into the swapchain.
        // Reading and writing the main attachment in one pass is illegal in Vulkan.
        RenderTarget snapshot = new TextureTarget("Flashback viewport snapshot", source.width, source.height, false);
        try {
            copy(source.getColorTextureView(), snapshot.getColorTextureView(), 0, 0, 1, 1, false);
            copy(snapshot.getColorTextureView(), Minecraft.getInstance().getMainRenderTarget().getColorTextureView(),
                    x1, y1, x2, y2, true);
        } finally { snapshot.destroyBuffers(); }
    }
}
