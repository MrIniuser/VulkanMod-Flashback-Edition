package net.vulkanmod.render.shader;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class CustomRenderPipelines {

    public static final List<RenderPipeline> pipelines = new ArrayList<>();

    public static final RenderPipeline.Snippet GUI_TRIANGLES_SNIPPET = RenderPipeline.builder(RenderPipelines.MATRICES_PROJECTION_SNIPPET)
                                                                           .withVertexShader("core/gui")
                                                                           .withFragmentShader("core/gui")
                                                                           .withBlend(BlendFunction.TRANSLUCENT)
                                                                           .withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.TRIANGLES)
                                                                           .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                                                                           .buildSnippet();

    public static final RenderPipeline GUI_TRIANGLES = register(RenderPipeline.builder(GUI_TRIANGLES_SNIPPET).withLocation("pipeline/gui").build());

    public static final RenderPipeline FLASHBACK_UI = register(RenderPipeline.builder()
            .withLocation(ResourceLocation.fromNamespaceAndPath("vulkanmod", "pipeline/flashback_ui"))
            .withVertexShader(ResourceLocation.fromNamespaceAndPath("vulkanmod", "core/flashback_ui"))
            .withFragmentShader(ResourceLocation.fromNamespaceAndPath("vulkanmod", "core/flashback_ui"))
            .withSampler("Sampler0")
            .withBlend(BlendFunction.TRANSLUCENT)
            .withDepthWrite(false).withCull(false)
            .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
            .withVertexFormat(DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.TRIANGLES)
            .build());

    public static final RenderPipeline FLASHBACK_COPY = register(RenderPipeline.builder()
            .withLocation(ResourceLocation.fromNamespaceAndPath("vulkanmod", "pipeline/flashback_copy"))
            .withVertexShader(ResourceLocation.fromNamespaceAndPath("vulkanmod", "core/flashback_ui"))
            .withFragmentShader(ResourceLocation.fromNamespaceAndPath("vulkanmod", "core/flashback_ui"))
            .withSampler("Sampler0")
            .withDepthWrite(false).withCull(false)
            .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
            .withVertexFormat(DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.TRIANGLES)
            .build());

    static RenderPipeline register(RenderPipeline pipeline) {
        pipelines.add(pipeline);
        return pipeline;
    }
}
