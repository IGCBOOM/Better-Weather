package corgitaco.betterweather.api.client.opengl;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface Destroyable {

    void destroy();
}
