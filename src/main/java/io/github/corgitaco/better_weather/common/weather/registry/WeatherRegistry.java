package io.github.corgitaco.better_weather.common.weather.registry;

import com.google.common.collect.Lists;
import io.github.corgitaco.better_weather.common.BetterWeather;
import io.github.corgitaco.better_weather.common.weather.Weather;
import net.minecraft.resources.ResourceLocation;

import java.util.Queue;

public final class WeatherRegistry<L extends ResourceLocation, W extends Weather> {
    public static final WeatherRegistry<ResourceLocation, Weather> INSTANCE = new WeatherRegistry<>();

    private final WeatherRegistryStorage<L, W> storage = new WeatherRegistryStorage<>();

    private final Queue<Task<WeatherRegistryStorage<L, W>>> tasks = Lists.newLinkedList();

    private WeatherRegistry() {
    }

    public void build() {
        long stamp = System.currentTimeMillis();

        tasks.forEach(task -> task.accept(storage));
        storage.build();

        BetterWeather.LOGGER.info("Registration took {} ms", System.currentTimeMillis() - stamp);
    }

    public WeatherRegistry<L, W> task(Task<WeatherRegistryStorage<L, W>> task) {
        tasks.offer(task);
        return this;
    }

    @FunctionalInterface
    public interface Task<S extends WeatherRegistryStorage<? extends ResourceLocation, ? extends Weather>> {
        void accept(S storage);
    }
}
