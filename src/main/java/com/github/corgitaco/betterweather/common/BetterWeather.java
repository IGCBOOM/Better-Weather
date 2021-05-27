package com.github.corgitaco.betterweather.common;

import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class BetterWeather implements ModInitializer {
    private static final String MOD_ID = "betterweather";

    @Override
    public void onInitialize() {
        WeatherRegistry.register();
    }

    @Contract("null -> fail; !null -> new")
    @NotNull
    public static ResourceLocation resourceLocation(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
