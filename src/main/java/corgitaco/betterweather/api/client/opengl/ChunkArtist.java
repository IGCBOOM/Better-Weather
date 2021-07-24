package corgitaco.betterweather.api.client.opengl;

import com.mojang.blaze3d.systems.RenderSystem;
import corgitaco.betterweather.api.client.opengl.glsl.GlslProgram;
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

    private GlslProgram program;

    public void bind() {
        Minecraft minecraft = Minecraft.getInstance();
        GameRenderer renderer = minecraft.gameRenderer;

        if (program == null) {
            program = GlslProgram.createFrom(sources -> {
                sources.compile(GL_FRAGMENT_SHADER, new ResourceLocation("betterweather", "glsl/chunk/fragment.glsl"));
                sources.compile(GL_VERTEX_SHADER, new ResourceLocation("betterweather", "glsl/chunk/vertex.glsl"));
            });
        }
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
        modelView.setIdentity();
        modelView.set(matrix4f);

        program.uploadMatrix4f("modelView", modelView);

        RenderSystem.pushMatrix();
        RenderSystem.loadIdentity();
        RenderSystem.multMatrix(matrix4f);

        glDrawArrays(GL_QUADS, 0, vertexCount);

        RenderSystem.popMatrix();
    }

    @Override
    public void destroy() {
        program.destroy();
    }
}
