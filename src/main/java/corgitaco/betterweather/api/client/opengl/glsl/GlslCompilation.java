package corgitaco.betterweather.api.client.opengl.glsl;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.io.IOException;

@OnlyIn(Dist.CLIENT)
@FunctionalInterface
public interface GlslCompilation {

    void compile(GlslSources sources) throws IOException;
}
