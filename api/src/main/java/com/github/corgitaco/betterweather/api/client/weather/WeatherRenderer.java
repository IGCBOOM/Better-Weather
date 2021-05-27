package com.github.corgitaco.betterweather.api.client.weather;

import com.github.corgitaco.betterweather.api.client.ColorProperties;
import com.github.corgitaco.betterweather.api.common.util.Builder;

public abstract class WeatherRenderer {
    private final ColorProperties properties;

    public WeatherRenderer(Builder<ColorProperties> builder) {
        properties = builder.build(new ColorProperties());
    }
}
