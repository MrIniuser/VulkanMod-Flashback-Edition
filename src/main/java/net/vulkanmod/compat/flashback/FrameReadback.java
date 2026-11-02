// SPDX-License-Identifier: LGPL-3.0-only
package net.vulkanmod.compat.flashback;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.TextureFormat;
import net.vulkanmod.render.engine.VkGpuTexture;
import net.vulkanmod.vulkan.Renderer;
import java.nio.ByteBuffer;
import org.lwjgl.vulkan.VK10;

/** Correctness-first, synchronous readback. No GL context, PBO or native pixel pointers. */
public final class FrameReadback implements AutoCloseable {
    private NativeImage pending;
    private boolean closed;

    public void start(GpuTexture texture, int width, int height) {
        RenderSystem.assertOnRenderThread();
        if (closed) throw new IllegalStateException("Readback is closed");
        if (pending != null) throw new IllegalStateException("Frame already pending");
        if (!(texture instanceof VkGpuTexture vk) || texture.isClosed())
            throw new IllegalArgumentException("Expected a live Vulkan texture");
        if (width <= 0 || height <= 0 || width != texture.getWidth(0) || height != texture.getHeight(0))
            throw new IllegalArgumentException("Readback dimensions must match the texture");
        if (texture.getFormat() != TextureFormat.RGBA8)
            throw new IllegalArgumentException("Only RGBA8/BGRA8 frame readback is supported");
        int bytes = FramePixels.checkedByteSize(width, height);
        // copyTextureToBuffer submits a separate transfer. Submit the producer first,
        // otherwise this can download a previous frame or race an unfinished pass.
        Renderer.getInstance().flushCmds();
        var encoder = RenderSystem.getDevice().createCommandEncoder();
        try (GpuBuffer buffer = RenderSystem.getDevice().createBuffer(
                () -> "Flashback Vulkan readback", GpuBuffer.USAGE_COPY_DST | GpuBuffer.USAGE_MAP_READ, bytes)) {
            // VulkanMod 0.5.7 waits on a fence and runs this callback synchronously.
            encoder.copyTextureToBuffer(texture, buffer, 0, () -> {}, 0);
            try (GpuBuffer.MappedView view = encoder.mapBuffer(buffer, true, false)) {
                ByteBuffer pixels = view.data();
                NativeImage image = new NativeImage(NativeImage.Format.RGBA, width, height, false);
                boolean bgra = vk.getVulkanImage().format == VK10.VK_FORMAT_B8G8R8A8_UNORM;
                try {
                    for (int y = 0; y < height; y++) {
                        for (int x = 0; x < width; x++) {
                            int offset = (x + y * width) * 4;
                            image.setPixelABGR(x, y, FramePixels.toABGR(pixels, offset, bgra));
                        }
                    }
                    pending = image;
                } catch (RuntimeException | Error error) { image.close(); throw error; }
            }
        }
    }

    public NativeImage finish(int width, int height) {
        if (closed || pending == null) throw new IllegalStateException("No pending frame");
        if (width != pending.getWidth() || height != pending.getHeight())
            throw new IllegalArgumentException("Frame size changed during readback");
        NativeImage result = pending;
        pending = null; // ownership passes to Flashback's existing export queue
        return result;
    }

    public void close() {
        if (pending != null) { pending.close(); pending = null; }
        closed = true;
    }
}
