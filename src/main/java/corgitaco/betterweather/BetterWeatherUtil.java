package corgitaco.betterweather;

import corgitaco.betterweather.api.SeasonData;
import corgitaco.betterweather.season.SubSeasonSettings;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;

import java.awt.*;

public class BetterWeatherUtil {

    public static final Color DEFAULT_RAIN_SKY = new Color(103, 114, 136);
    public static final Color DEFAULT_RAIN_FOG = new Color(89, 100, 142);
    public static final Color DEFAULT_RAIN_CLOUDS = new Color(158, 158, 158);

    public static final Color DEFAULT_THUNDER_SKY = new Color(42, 45, 51);
    public static final Color DEFAULT_THUNDER_FOG = new Color(85, 95, 135);
    public static final Color DEFAULT_THUNDER_CLOUDS = new Color(37, 37, 37);

    public static int parseHexColor(String targetHexColor) {
        if (!targetHexColor.isEmpty()) {
            try {
                return (int) Long.parseLong(targetHexColor.replace("#", "").replace("0x", ""), 16);
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        }
        return Integer.MAX_VALUE;
    }

    public static boolean filterRegistryID(ResourceLocation id, Registry<?> registry, String registryTypeName) {
        if (registry.keySet().contains(id))
            return true;
        else {
            BetterWeather.LOGGER.error("\"" + id.toString() + "\" was not a registryID in the " + registryTypeName + "! Skipping entry...");
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    public static <I, O> O unsafeCast(I obj) {
        return (O) obj;
    }

    public static int transformRainOrThunderTimeToCurrentSeason(int rainOrThunderTime, SubSeasonSettings previous, SubSeasonSettings current) {
        double previousMultiplier = previous.getWeatherEventChanceMultiplier();
        double currentMultiplier = current.getWeatherEventChanceMultiplier();
        double normalTime = rainOrThunderTime * previousMultiplier;

        return (int) (normalTime * 1 / currentMultiplier);
    }

    public static int modifiedColorValue(int original, int target, double blendStrength) {
        return (int) MathHelper.lerp(blendStrength, original, target);
    }

    public static Color blendColor(Color original, Color target, double blendStrength) {
        int modifiedRed = modifiedColorValue(original.getRed(), target.getRed(), blendStrength);
        int modifiedGreen = modifiedColorValue(original.getGreen(), target.getGreen(), blendStrength);
        int modifiedBlue = modifiedColorValue(original.getBlue(), target.getBlue(), blendStrength);
        return new Color(modifiedRed, modifiedGreen, modifiedBlue);
    }

    public static Color transformFloatColor(Vector3d floatColor) {
        return new Color((int) (floatColor.getX() * 255), (int) (floatColor.getY() * 255), (int) (floatColor.getZ() * 255));
    }

    public static SeasonData.SeasonKey getSeasonFromTime(int seasonTimeInCycle, int seasonCycleLength) {
        int seasonLength = seasonCycleLength / 4;

        if (seasonTimeInCycle < seasonLength) {
            return SeasonData.SeasonKey.SPRING;
        } else if (seasonTimeInCycle < seasonLength * 2) {
            return SeasonData.SeasonKey.SUMMER;
        } else if (seasonTimeInCycle < seasonLength * 3) {
            return SeasonData.SeasonKey.AUTUMN;
        } else
            return SeasonData.SeasonKey.WINTER;
    }

    public static int getTimeInCycleForSeason(SeasonData.SeasonKey subSeasonVal, int seasonCycleLength) {
        int perSubSeasonLength = getSubSeasonLength(seasonCycleLength);
        return perSubSeasonLength * subSeasonVal.ordinal();
    }

    private static int getSubSeasonLength(int seasonCycleLength) {
        return seasonCycleLength / (SeasonData.SeasonKey.values().length);
    }

    public static int getTimeInCycleForSeasonAndPhase(SeasonData.SeasonKey subSeasonVal, SeasonData.Phase phase, int seasonCycleLength) {
        int timeInCycleForSeason = getTimeInCycleForSeason(subSeasonVal, seasonCycleLength);
        int phaseTime = getSubSeasonLength(seasonCycleLength) / SeasonData.Phase.values().length;
        return timeInCycleForSeason + (phaseTime * phase.ordinal());
    }
}