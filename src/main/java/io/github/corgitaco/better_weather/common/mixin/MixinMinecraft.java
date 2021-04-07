package io.github.corgitaco.better_weather.common.mixin;

import io.github.corgitaco.better_weather.common.weather.registry.WeatherRegistry;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft {

    @Inject(method = "run", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;gameThread:Ljava/lang/Thread;", shift = At.Shift.AFTER))
    public void run(CallbackInfo info) {
        // vanilla registration happens on the client (main thread), will do the same
        WeatherRegistry.INSTANCE.build();
    }
}
