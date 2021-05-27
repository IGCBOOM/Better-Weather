package com.github.corgitaco.betterweather.common;

import com.github.corgitaco.betterweather.api.common.weather.ClearWeather;
import com.github.corgitaco.betterweather.api.common.weather.RainWeather;
import com.github.corgitaco.betterweather.api.common.weather.Weather;
import com.github.corgitaco.betterweather.common.weather.AcidRainWeather;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

public final class WeatherRegistry {
    public static final DefaultedRegistry<Weather> WEATHER = FabricRegistryBuilder.createDefaulted(Weather.class, BetterWeather.resourceLocation("weather"), new ResourceLocation("clear")).buildAndRegister();

    public static final Weather CLEAR = new ClearWeather();
    public static final Weather RAIN = new RainWeather();

    public static final Weather ACID_RAIN = new AcidRainWeather();

    private WeatherRegistry() {
    }

    public static void register() {
        // Vanilla
        Registry.register(WEATHER, new ResourceLocation("clear"), CLEAR);
        Registry.register(WEATHER, new ResourceLocation("rain"), RAIN);

        // Custom
        register("acid_rain", ACID_RAIN);
    }

    private static <T extends Weather> void register(String key, T weather) {
        Registry.register(WEATHER, BetterWeather.resourceLocation(key), weather);
    }
}
