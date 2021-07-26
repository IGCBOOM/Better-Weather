package corgitaco.betterweather.api.client.opengl;

import com.mojang.blaze3d.systems.RenderSystem;
import corgitaco.betterweather.api.client.opengl.glsl.GlslProgram;
import corgitaco.betterweather.api.client.opengl.glsl.LazyGlslProgram;
import corgitaco.betterweather.helpers.BetterWeatherWorldData;
import corgitaco.betterweather.season.BWSubseasonSettings;
import corgitaco.betterweather.season.SeasonContext;
import corgitaco.betterweather.util.client.ColorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Stack;

import static org.lwjgl.opengl.GL20.*;

@OnlyIn(Dist.CLIENT)
public class ChunkArtist implements Destroyable {
    private final Stack<BlockPos> chunkPosStack = new Stack<>();

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
        BlockPos chunkPos = chunkPosStack.pop();

        GlslProgram program = lazy.get();

        getTint(program);

        program.upload1i("textureSampler", 0);
        //program.upload1i("lightmapSampler", 1);

        modelView.setIdentity();
        modelView.set(matrix4f);

        program.uploadMatrix4f("modelView", modelView);

        RenderSystem.pushMatrix();
        RenderSystem.loadIdentity();
        RenderSystem.multMatrix(matrix4f);

        glDrawArrays(GL_QUADS, 0, vertexCount);

        RenderSystem.popMatrix();
    }

    public void getTint(GlslProgram program) {
        float[] rgb = {
                1.0F,
                1.0F,
                1.0F
        };

        Minecraft minecraft = Minecraft.getInstance();

        ClientWorld world = minecraft.world;
        if (world == null) {
            return;
        }

        SeasonContext seasonContext = ((BetterWeatherWorldData) world).getSeasonContext();

        if (seasonContext != null) {
            BWSubseasonSettings settings = seasonContext.getCurrentSubSeasonSettings();

            int[] rgb3i = ColorUtil.unpack(settings.getClientSettings().getTargetGrassHexColor());

            rgb[0] = (float) rgb3i[0] / 0xFF;
            rgb[1] = (float) rgb3i[1] / 0xFF;
            rgb[2] = (float) rgb3i[2] / 0xFF;
        }

        program.upload4f("rgba", rgb[0], rgb[1], rgb[2], 1.0F);
    }

    public void push(BlockPos pos) {
        chunkPosStack.push(pos);
    }

    @Override
    public void destroy() {
        lazy.ifPresent(GlslProgram::destroy);
    }
}
