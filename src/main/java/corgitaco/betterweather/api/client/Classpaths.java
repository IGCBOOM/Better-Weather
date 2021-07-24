package corgitaco.betterweather.api.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.nio.file.Path;
import java.nio.file.Paths;

@OnlyIn(Dist.CLIENT)
public final class Classpaths {
    private static final String[] PATHS = System.getProperty("java.class.path").split(System.getProperty("path.separator"));

    public static final Path CLASSES = Paths.get(PATHS[0]);
    public static final Path RESOURCES = Paths.get(PATHS[1]);

    private Classpaths() {
    }
}
