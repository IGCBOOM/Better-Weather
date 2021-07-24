package corgitaco.betterweather.mixin.client;

import corgitaco.betterweather.api.client.opengl.ChunkArtist;
import corgitaco.betterweather.api.client.opengl.DestroyableGarbage;
import corgitaco.betterweather.api.client.opengl.VertexArrayObject;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;

@OnlyIn(Dist.CLIENT)
@Mixin(VertexBuffer.class)
public class MixinVertexBuffer implements VertexArrayObject {
    private final int vao = glGenVertexArrays();

    @Shadow @Final private VertexFormat vertexFormat;
    @Shadow private int count;
    private static final Predicate<VertexFormat> IS_USED_WITH_CHUNKS = vertexFormat -> vertexFormat == DefaultVertexFormats.BLOCK;

    @Inject(at = @At("HEAD"), method = "uploadRaw")
    public void bindVboToVaoBind(BufferBuilder bufferIn, CallbackInfo ci) {
        bindVao();
    }

    @Inject(at = @At("RETURN"), method = "uploadRaw")
    public void bindVboToVaoUnbound(BufferBuilder bufferIn, CallbackInfo ci) {
        VertexArrayObject.unbindVao();
    }

    @Inject(at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;glBufferData(ILjava/nio/ByteBuffer;I)V", shift = At.Shift.AFTER), method = "uploadRaw")
    public void uploadRaw(BufferBuilder bufferIn, CallbackInfo ci) {
        if (IS_USED_WITH_CHUNKS.test(vertexFormat)) {
            int i = vertexFormat.getSize();

            glVertexAttribPointer(0, 3, GL_FLOAT, false, i, 0);
            glVertexAttribPointer(1, 3, GL_FLOAT, false, i, 3 * 4);
        }
    }

    @Inject(at = @At("INVOKE"), method = "draw", cancellable = true)
    public void draw(Matrix4f matrixIn, int modeIn, CallbackInfo ci) {
        if (IS_USED_WITH_CHUNKS.test(vertexFormat)) {
            DestroyableGarbage destroyableGarbage = DestroyableGarbage.INSTANCE.get();

            ChunkArtist artist = destroyableGarbage.getChunkArtist();

            artist.draw(matrixIn, count);

            ci.cancel();
        }
    }

    @Override
    public void bindVao() {
        glBindVertexArray(vao);
    }

    @Inject(at = @At("RETURN"), method = "close")
    public void close(CallbackInfo ci) {
        glDeleteVertexArrays(vao);
    }
}
