package io.github.corgitaco.better_weather.common.weather.registry;

import io.github.corgitaco.better_weather.common.BetterWeather;
import io.github.corgitaco.better_weather.common.weather.Weather;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.LinkedList;
import java.util.Queue;

public final class WeatherRegistry<L extends ResourceLocation, W extends Weather> {
    public static final WeatherRegistry<ResourceLocation, Weather> INSTANCE = new WeatherRegistry<>();

    private final WeatherRegistryStorage<L, W> storage = new WeatherRegistryStorage<>();

    private final Queue<Task<WeatherRegistryStorage<L, W>>> tasks = new LinkedList<>();

    private WeatherRegistry() {
    }

    /**
     * Should not be called outside of {@link io.github.corgitaco.better_weather.common.mixin.MixinMinecraft#run(CallbackInfo)}
     */
    public void build() {
        long stamp = System.currentTimeMillis();

        tasks.forEach(task -> task.accept(storage));
        storage.build();

        BetterWeather.LOGGER.info("Registration took {} ms", System.currentTimeMillis() - stamp);
    }

    public void task(Task<WeatherRegistryStorage<L, W>> task) {
        tasks.offer(task);
    }

    @FunctionalInterface
    public interface Task<S extends WeatherRegistryStorage<? extends ResourceLocation, ? extends Weather>> {
        void accept(S storage);
    }
}
