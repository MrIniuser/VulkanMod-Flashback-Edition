// SPDX-License-Identifier: LGPL-3.0-only
package net.vulkanmod.compat.flashback;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.TextureFormat;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import imgui.moulberry90.ImDrawData;
import imgui.moulberry90.ImGui;
import imgui.moulberry90.ImVec4;
import imgui.moulberry90.flag.ImGuiBackendFlags;
import imgui.moulberry90.flag.ImGuiConfigFlags;
import imgui.moulberry90.type.ImInt;
import net.minecraft.client.Minecraft;
import net.vulkanmod.render.engine.VkGpuTexture;
import net.vulkanmod.render.shader.CustomRenderPipelines;
import org.lwjgl.system.MemoryUtil;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.OptionalInt;

/** ImGui draw-list adapter to Minecraft's GPU API. Main window only. */
public final class FlashbackImGuiRenderer implements AutoCloseable {
    private GpuTexture font;
    private final ImVec4 clip = new ImVec4();

    public boolean init() {
        var io = ImGui.getIO();
        io.setBackendRendererName("vulkanmod_flashback_experimental");
        io.addBackendFlags(ImGuiBackendFlags.RendererHasVtxOffset);
        io.removeBackendFlags(ImGuiBackendFlags.RendererHasViewports);
        io.removeConfigFlags(ImGuiConfigFlags.ViewportsEnable);
        return true;
    }
    public void newFrame() {
        // No secondary swapchains: keep dockable panels inside the Minecraft window.
        ImGui.getIO().removeConfigFlags(ImGuiConfigFlags.ViewportsEnable);
        if (font == null) updateFonts();
    }
    public void updateFonts() {
        RenderSystem.assertOnRenderThread();
        ImInt width = new ImInt(), height = new ImInt();
        var atlas = ImGui.getIO().getFonts();
        ByteBuffer pixels = atlas.getTexDataAsRGBA32(width, height);
        GpuTexture replacement = RenderSystem.getDevice().createTexture(() -> "Flashback ImGui font atlas",
                GpuTexture.USAGE_COPY_DST | GpuTexture.USAGE_TEXTURE_BINDING,
                TextureFormat.RGBA8, width.get(), height.get(), 1, 1);
        try {
            replacement.setAddressMode(AddressMode.CLAMP_TO_EDGE);
            replacement.setTextureFilter(FilterMode.LINEAR, false);
            RenderSystem.getDevice().createCommandEncoder().writeToTexture(replacement, pixels,
                    NativeImage.Format.RGBA, 0, 0, 0, 0, width.get(), height.get());
            atlas.setTexID(((VkGpuTexture)replacement).glId());
        } catch (RuntimeException | Error e) { replacement.close(); throw e; }
        if (font != null) font.close();
        font = replacement;
    }
    public void destroyFonts() {
        if (font != null) { font.close(); font = null; }
        ImGui.getIO().getFonts().setTexID(0);
    }
    public void render(ImDrawData data) {
        RenderSystem.assertOnRenderThread();
        float width = data.getDisplaySizeX(), height = data.getDisplaySizeY();
        int framebufferW = (int)(width * data.getFramebufferScaleX());
        int framebufferH = (int)(height * data.getFramebufferScaleY());
        if (framebufferW <= 0 || framebufferH <= 0 || data.getCmdListsCount() == 0) return;
        if (ImDrawData.sizeOfImDrawVert() != 20)
            throw new IllegalStateException("Unsupported ImGui vertex ABI; expected Flashback's 20-byte ImDrawVert");
        int indexSize = ImDrawData.sizeOfImDrawIdx();
        if (indexSize != 2 && indexSize != 4) throw new IllegalStateException("Unsupported ImGui index ABI");
        var target = Minecraft.getInstance().getMainRenderTarget().getColorTextureView();
        // VulkanMod's main-target rebind deliberately does not reset viewport state.
        GlStateManager._viewport(0, 0, target.texture().getWidth(0), target.texture().getHeight(0));
        for (int list = 0; list < data.getCmdListsCount(); list++) {
            int count = data.getCmdListVtxBufferSize(list);
            if (count == 0 || data.getCmdListIdxBufferSize(list) == 0) continue;
            ByteBuffer input = data.getCmdListVtxBufferData(list).duplicate().order(ByteOrder.nativeOrder());
            ByteBuffer converted = MemoryUtil.memAlloc(Math.multiplyExact(count,
                    DefaultVertexFormat.POSITION_TEX_COLOR.getVertexSize()));
            try {
                // ImDrawVert = vec2 position, vec2 uv, RGBA8. Minecraft needs vec3 position.
                UiGeometry.convertVertices(input, converted, count,
                        data.getDisplayPosX(), data.getDisplayPosY(), width, height);
                converted.flip();
                try (GpuBuffer vertices = RenderSystem.getDevice().createBuffer(() -> "Flashback ImGui vertices",
                            GpuBuffer.USAGE_VERTEX | GpuBuffer.USAGE_COPY_DST, converted);
                     GpuBuffer indices = RenderSystem.getDevice().createBuffer(() -> "Flashback ImGui indices",
                            GpuBuffer.USAGE_INDEX | GpuBuffer.USAGE_COPY_DST, data.getCmdListIdxBufferData(list));
                     var pass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(
                            () -> "Flashback ImGui", target, OptionalInt.empty())) {
                    pass.setPipeline(CustomRenderPipelines.FLASHBACK_UI);
                    pass.setVertexBuffer(0, vertices);
                    pass.setIndexBuffer(indices, indexSize == 2 ? VertexFormat.IndexType.SHORT : VertexFormat.IndexType.INT);
                    for (int command = 0; command < data.getCmdListCmdBufferSize(list); command++) {
                        int elements = data.getCmdListCmdBufferElemCount(list, command);
                        if (elements == 0) continue;
                        data.getCmdListCmdBufferClipRect(clip, list, command);
                        var scissor = UiGeometry.clip(clip.x, clip.y, clip.z, clip.w,
                                data.getDisplayPosX(), data.getDisplayPosY(),
                                data.getFramebufferScaleX(), data.getFramebufferScaleY(), framebufferW, framebufferH);
                        if (scissor == null) continue;
                        long id = data.getCmdListCmdBufferTextureId(list, command);
                        VkGpuTexture texture = VkGpuTexture.findLiveTexture((int)id);
                        if (id != (long)(int)id || texture == null || texture.isClosed())
                            throw new IllegalStateException("Unknown Flashback ImGui texture ID: " + id);
                        try (var view = RenderSystem.getDevice().createTextureView(texture)) {
                            pass.bindSampler("Sampler0", view);
                            // The Minecraft GPU facade retains GL-style bottom-left scissors;
                            // VulkanMod converts them back to a top-left Vulkan rectangle.
                            pass.enableScissor(scissor.x(), scissor.y(), scissor.width(), scissor.height());
                            pass.drawIndexed(data.getCmdListCmdBufferVtxOffset(list, command),
                                    data.getCmdListCmdBufferIdxOffset(list, command), elements, 1);
                        }
                    }
                    pass.disableScissor();
                }
            } finally { MemoryUtil.memFree(converted); }
        }
    }
    public void close() {
        destroyFonts();
        var io = ImGui.getIO();
        io.removeBackendFlags(ImGuiBackendFlags.RendererHasVtxOffset | ImGuiBackendFlags.RendererHasViewports);
        io.setBackendRendererName(null);
    }
}
