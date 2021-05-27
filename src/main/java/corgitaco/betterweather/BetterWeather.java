package corgitaco.betterweather;

import corgitaco.betterweather.api.BetterWeatherRegistry;
import corgitaco.betterweather.data.network.NetworkHandler;
import corgitaco.betterweather.server.BetterWeatherGameRules;
import corgitaco.betterweather.weather.event.Blizzard;
import corgitaco.betterweather.weather.event.None;
import corgitaco.betterweather.weather.event.client.BlizzardClientSettings;
import corgitaco.betterweather.weather.event.client.NoneClientSettings;
import net.minecraft.util.LazyValue;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.nio.file.Path;

@Mod("betterweather")
public class BetterWeather {
    public static final String MOD_ID = "betterweather";
    public static final Path CONFIG_PATH = new File(String.valueOf(FMLPaths.CONFIGDIR.get().resolve(MOD_ID))).toPath();
    public static final Logger LOGGER = LogManager.getLogger();

    public static final boolean USING_OPTIFINE = new LazyValue<>(() -> {
        try {
            return Class.forName("net.optifine.Config") != null;
        } catch (final Exception e) {
            return false;
        }
    }).getValue();

    public BetterWeather() {
        if (!CONFIG_PATH.toFile().exists())
            CONFIG_PATH.toFile().mkdir();

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::commonSetup);
        bus.addListener(this::clientSetup);
        bus.addListener(this::lateSetup);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(BetterWeatherGameRules::init);
        NetworkHandler.init();

        Registry.register(BetterWeatherRegistry.CLIENT_WEATHER_EVENT_SETTINGS, new ResourceLocation(MOD_ID, "blizzard"), BlizzardClientSettings.CODEC);
        Registry.register(BetterWeatherRegistry.CLIENT_WEATHER_EVENT_SETTINGS, new ResourceLocation(MOD_ID, "none"), NoneClientSettings.CODEC);
        Registry.register(BetterWeatherRegistry.WEATHER_EVENT, new ResourceLocation(MOD_ID, "blizzard"), Blizzard.CODEC);
        Registry.register(BetterWeatherRegistry.WEATHER_EVENT, new ResourceLocation(MOD_ID, "none"), None.CODEC);
    }

    private void clientSetup(FMLClientSetupEvent event) {
        if (USING_OPTIFINE) {
            LOGGER.info("Optifine is loaded.");
        }
    }

    private void lateSetup(FMLLoadCompleteEvent event) {

    }
}
