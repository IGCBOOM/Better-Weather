package com.github.corgitaco.betterweather.api.client;

import com.github.corgitaco.betterweather.api.client.weather.WeatherRenderer;
import com.github.corgitaco.betterweather.api.common.weather.Weather;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;

import java.util.Map;

public final class Dispatcher {
    private static final Map<Weather, WeatherRenderer> RENDERERS = new Reference2ObjectOpenHashMap<>();

    private Dispatcher() {
    }

    public static void register(Weather weather, WeatherRenderer renderer) {
        RENDERERS.putIfAbsent(weather, renderer);
    }
}
