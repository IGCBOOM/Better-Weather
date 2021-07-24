package corgitaco.betterweather.api.client.opengl;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public interface DestroyableGarbage {
    Supplier<DestroyableGarbage> INSTANCE = () -> (DestroyableGarbage) Minecraft.getInstance().worldRenderer;

    <T extends Destroyable> T push(T destroyable);

    @NotNull ChunkArtist getChunkArtist();
}
