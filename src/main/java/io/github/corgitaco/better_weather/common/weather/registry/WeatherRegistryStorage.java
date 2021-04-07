package io.github.corgitaco.better_weather.common.weather.registry;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import io.github.corgitaco.better_weather.common.BetterWeather;
import io.github.corgitaco.better_weather.common.weather.Weather;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.Queue;

public final class WeatherRegistryStorage<L extends ResourceLocation, W extends Weather> {
    private final Queue<ImmutableEntry<L, W>> queue = new LinkedList<>();
    private @Nullable BiMap<L, W> storage;

    protected WeatherRegistryStorage() {
    }

    protected void build() {
        ImmutableBiMap.Builder<L, W> builder = ImmutableBiMap.builder();

        queue.forEach(entry -> builder.put(entry.getLocation(), entry.getWeather()));
        // queue is now empty

        storage = builder.build();
    }

    public W register(L location, W weather) {
        queue.offer(new ImmutableEntry<>(location, weather));

        BetterWeather.LOGGER.info("Successfully registered {}", location.toString());

        return weather;
    }

    private static final class ImmutableEntry<L extends ResourceLocation, W extends Weather> {
        @Getter
        private final L location;

        @Getter
        private final W weather;

        private ImmutableEntry(L location, W weather) {
            this.location = location;
            this.weather = weather;
        }
    }
}
