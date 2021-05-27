package com.github.corgitaco.betterweather.client.weather;

import com.github.corgitaco.betterweather.api.client.ColorProperties;
import com.github.corgitaco.betterweather.api.client.weather.RainWeatherRenderer;
import com.github.corgitaco.betterweather.api.common.util.Builder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class AcidRainWeatherRenderer extends RainWeatherRenderer {

    public AcidRainWeatherRenderer(Builder<ColorProperties> builder) {
        super(builder);
    }
}
