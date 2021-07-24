package corgitaco.betterweather.api.client.opengl;

import com.mojang.blaze3d.systems.RenderSystem;
import corgitaco.betterweather.api.client.opengl.glsl.GlslProgram;
import corgitaco.betterweather.api.client.opengl.glsl.LazyGlslProgram;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static org.lwjgl.opengl.GL20.*;

@OnlyIn(Dist.CLIENT)
public class ChunkArtist implements Destroyable {
    private final Matrix4f projection = new Matrix4f();
    private final Matrix4f modelView = new Matrix4f();

    private final LazyGlslProgram lazy = new LazyGlslProgram(sources -> {
        sources.compile(GL_FRAGMENT_SHADER, new ResourceLocation("betterweather", "glsl/chunk/fragment.glsl"));
        sources.compile(GL_VERTEX_SHADER, new ResourceLocation("betterweather", "glsl/chunk/vertex.glsl"));
    });

    public void bind() {
        Minecraft minecraft = Minecraft.getInstance();
        GameRenderer renderer = minecraft.gameRenderer;

        GlslProgram program = lazy.create();

        program.bind();

        projection.setIdentity();
        projection.mul(renderer.getProjectionMatrix(renderer.getActiveRenderInfo(), minecraft.getRenderPartialTicks(), true));

        program.uploadMatrix4f("projection", projection);
    }

    public void unbind() {
        GlslProgram.unbind();
    }

    @SuppressWarnings("deprecation")
    public void draw(Matrix4f matrix4f, int vertexCount) {
        GlslProgram program = lazy.get();

        modelView.setIdentity();
        modelView.set(matrix4f);

        program.uploadMatrix4f("modelView", modelView);

        RenderSystem.pushMatrix();
        RenderSystem.loadIdentity();
        RenderSystem.multMatrix(matrix4f);

        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawArrays(GL_QUADS, 0, vertexCount);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);

        RenderSystem.popMatrix();
    }

    @Override
    public void destroy() {
        lazy.ifPresent(GlslProgram::destroy);
    }
}
