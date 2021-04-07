package io.github.corgitaco.better_weather.client.graphics;

import lombok.experimental.UtilityClass;
import net.fabricmc.api.Environment;

import static net.fabricmc.api.EnvType.CLIENT;

// packing/unpacking of alpha isn't needed, but if doing so use (a & HEX) << 24 / (rgba >> 24) & HEX

@Environment(CLIENT)

@UtilityClass
public class Colour {
    private final int MASK = 0xFF; // 255 bit mask

    public int pack(int r, int g, int b) {
        return (r & MASK) << 16 | (g & MASK) << 8 | b & MASK;
    }
    
    public int[] unpack(int rgb) {
        return new int[] {(rgb >> 16) & MASK, (rgb >> 8) & MASK, rgb & MASK};
    }
}
