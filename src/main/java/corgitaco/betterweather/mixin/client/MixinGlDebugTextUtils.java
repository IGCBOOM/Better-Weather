package corgitaco.betterweather.mixin.client;

import net.minecraft.client.renderer.GlDebugTextUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(GlDebugTextUtils.class)
public class MixinGlDebugTextUtils {

    // @Inject(at = @At("RETURN"), method = "logDebugMessage")
    // private static void logDebugMessage(int source, int type, int id, int severity, int messageLength, long message, long p_209244_7_, CallbackInfo ci) {
        // throw new IllegalStateException();
    // }
}
