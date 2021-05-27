package com.github.corgitaco.betterweather.client;

import com.github.corgitaco.betterweather.api.client.Dispatcher;
import com.github.corgitaco.betterweather.api.client.weather.ClearWeatherRenderer;
import com.github.corgitaco.betterweather.api.client.weather.RainWeatherRenderer;
import com.github.corgitaco.betterweather.client.weather.AcidRainWeatherRenderer;
import com.github.corgitaco.betterweather.common.WeatherRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class BetterWeatherClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        Dispatcher.register(WeatherRegistry.CLEAR, new ClearWeatherRenderer(properties -> properties));
        Dispatcher.register(WeatherRegistry.RAIN, new RainWeatherRenderer(properties -> properties));

        Dispatcher.register(WeatherRegistry.ACID_RAIN, new AcidRainWeatherRenderer(properties -> properties));
    }
}
