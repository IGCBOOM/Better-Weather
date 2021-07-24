package corgitaco.betterweather.mixin.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import corgitaco.betterweather.api.client.ColorSettings;
import corgitaco.betterweather.api.client.opengl.ChunkArtist;
import corgitaco.betterweather.api.client.opengl.Destroyable;
import corgitaco.betterweather.api.client.opengl.DestroyableGarbage;
import corgitaco.betterweather.api.client.opengl.VertexArrayObject;
import corgitaco.betterweather.helpers.BetterWeatherWorldData;
import corgitaco.betterweather.mixin.access.Vector3dAccess;
import corgitaco.betterweather.weather.BWWeatherEventContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Stack;

import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;

@Mixin(WorldRenderer.class)
public abstract class MixinWorldRenderer implements DestroyableGarbage {
    private final Stack<Destroyable> destroyableGarbage = new Stack<>();

    private final ChunkArtist chunkArtist = push(new ChunkArtist());

    @Shadow private int ticks;
    @Shadow @Final private Minecraft mc;
    @Shadow private ClientWorld world;

    @Inject(at = @At("HEAD"), method = "renderRainSnow(Lnet/minecraft/client/renderer/LightTexture;FDDD)V", cancellable = true)
    private void renderWeather(LightTexture lightmapIn, float partialTicks, double x, double y, double z, CallbackInfo ci) {
        BWWeatherEventContext weatherEventContext = ((BetterWeatherWorldData) this.world).getWeatherEventContext();
        if (weatherEventContext != null) {
            if (weatherEventContext.getCurrentClientEvent().renderWeather(mc, this.world, lightmapIn, ticks, partialTicks, x, y, z, weatherEventContext.getCurrentEvent()::isValidBiome)) {
                ci.cancel();
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "addRainParticles(Lnet/minecraft/client/renderer/ActiveRenderInfo;)V", cancellable = true)
    private void stopRainParticles(ActiveRenderInfo activeRenderInfoIn, CallbackInfo ci) {
        BWWeatherEventContext weatherEventContext = ((BetterWeatherWorldData) this.world).getWeatherEventContext();
        if (mc.world != null && weatherEventContext != null) {
            if (weatherEventContext.getCurrentClientEvent().weatherParticlesAndSound(activeRenderInfoIn, this.mc, this.ticks, weatherEventContext.getCurrentEvent()::isValidBiome)) {
                ci.cancel();
            }
        }
    }

    @Redirect(method = "renderSky(Lcom/mojang/blaze3d/matrix/MatrixStack;F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/ClientWorld;getRainStrength(F)F"))
    public float sunRemoval(ClientWorld clientWorld, float delta) {
        float rainStrength = this.world.getRainStrength(delta);
        BWWeatherEventContext weatherEventContext = ((BetterWeatherWorldData) this.world).getWeatherEventContext();
        return weatherEventContext != null ? rainStrength * weatherEventContext.getCurrentClientEvent().skyOpacity(clientWorld, this.mc.player.getPosition(), weatherEventContext.getCurrentEvent()::isValidBiome) : rainStrength;
    }

    @Inject(method = "drawClouds", at = @At(value = "HEAD"), cancellable = true)
    private void modifyCloudColor(BufferBuilder bufferIn, double cloudsX, double cloudsY, double cloudsZ, Vector3d cloudsColor, CallbackInfo ci) {
        BWWeatherEventContext weatherEventContext = ((BetterWeatherWorldData) this.world).getWeatherEventContext();
        if (weatherEventContext != null) {
            ColorSettings colorSettings = weatherEventContext.getCurrentEvent().getClientSettings().getColorSettings();
            double cloudColorBlendStrength = colorSettings.getCloudColorBlendStrength();
            if (cloudColorBlendStrength <= 0.0) {
                return;
            }

            int targetCloudHexColor = colorSettings.getTargetCloudHexColor();

            float r = (float) (targetCloudHexColor >> 16 & 255) / 255.0F;
            float g = (float) (targetCloudHexColor >> 8 & 255) / 255.0F;
            float b = (float) (targetCloudHexColor & 255) / 255.0F;

            float blendStrengthAtLocation = weatherEventContext.getCurrentClientEvent().cloudBlendStrength(this.world, new BlockPos(cloudsX, cloudsY, cloudsZ), weatherEventContext.getCurrentEvent()::isValidBiome);
            float rainStrength = this.world.getRainStrength(Minecraft.getInstance().getRenderPartialTicks());

            float blend = (float) Math.min(cloudColorBlendStrength, rainStrength * blendStrengthAtLocation);
            ((Vector3dAccess) cloudsColor).setX(MathHelper.lerp(blend, cloudsColor.x, r));
            ((Vector3dAccess) cloudsColor).setY(MathHelper.lerp(blend, cloudsColor.y, g));
            ((Vector3dAccess) cloudsColor).setZ(MathHelper.lerp(blend, cloudsColor.z, b));
        }
    }

    @Inject(at = @At("HEAD"), method = "renderBlockLayer")
    public void bindChunkArtist(RenderType blockLayerIn, MatrixStack matrixStackIn, double xIn, double yIn, double zIn, CallbackInfo ci) {
        chunkArtist.bind();
    }

    @Inject(at = @At("RETURN"), method = "renderBlockLayer")
    public void unbindChunkArtist(RenderType blockLayerIn, MatrixStack matrixStackIn, double xIn, double yIn, double zIn, CallbackInfo ci) {
        chunkArtist.unbind();
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/vertex/VertexBuffer;bindBuffer()V"), method = "renderBlockLayer")
    public void disableBind(VertexBuffer vertexBuffer) {
        ((VertexArrayObject) vertexBuffer).bindVao();
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/vertex/VertexBuffer;unbindBuffer()V"), method = "renderBlockLayer")
    public void disableUnbind() {
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        VertexArrayObject.unbindVao();
    }

    @Override
    public <T extends Destroyable> T push(T destroyable) {
        destroyableGarbage.push(destroyable);
        return destroyable;
    }

    @Override
    public @NotNull ChunkArtist getChunkArtist() {
        return chunkArtist;
    }

    @Inject(at = @At("INVOKE"), method = "close")
    public void close(CallbackInfo ci) {
        while (!destroyableGarbage.isEmpty()) {
            destroyableGarbage.pop().destroy();
        }
    }
}