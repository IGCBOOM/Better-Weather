package corgitaco.betterweather.api.client.opengl;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static org.lwjgl.opengl.GL30.glBindVertexArray;

@OnlyIn(Dist.CLIENT)
public interface VertexArrayObject {

    void bindVao();

    static void unbindVao() {
        glBindVertexArray(0);
    }
}
