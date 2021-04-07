package io.github.corgitaco.better_weather.common;

import io.github.corgitaco.better_weather.common.weather.RainWeather;
import io.github.corgitaco.better_weather.common.weather.Weather;
import io.github.corgitaco.better_weather.common.weather.registry.WeatherRegistry;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import static org.apache.logging.log4j.LogManager.getLogger;

public class BetterWeather implements ModInitializer {
    private static final String MOD_ID = "better_weather";

    public static final Logger LOGGER = getLogger("Better Weather");

    @Nullable
    public static Weather RAIN;

    @Override
    public void onInitialize() {
        WeatherRegistry.INSTANCE.task(storage -> {
            RAIN = storage.register(new ResourceLocation("rain"), new RainWeather());
        }).build();
    }

    public static ResourceLocation createLocation(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
