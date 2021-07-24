package corgitaco.betterweather.api.client.opengl.glsl;

import corgitaco.betterweather.BetterWeather;
import corgitaco.betterweather.api.client.opengl.Destroyable;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.function.IntConsumer;
import java.util.function.IntPredicate;

import static org.lwjgl.opengl.GL20.*;

@OnlyIn(Dist.CLIENT)
public final class GlslSources implements Destroyable {
    private final IntSet shaders = new IntOpenHashSet();

    public void compile(int type, ResourceLocation location) throws IOException {
        int shader = glCreateShader(type);

        glShaderSource(shader, readResource(location));

        glCompileShader(shader);
        if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_TRUE) {
            shaders.add(shader);
        } else {
            String infoLog = glGetShaderInfoLog(shader);
            glDeleteShader(shader);

            BetterWeather.LOGGER.error(infoLog);
        }
    }

    private static String readResource(ResourceLocation location) throws IOException {
        IResourceManager resourceManager = Minecraft.getInstance().getResourceManager();

        IResource resource = resourceManager.getResource(location);

        try (InputStream inputStream = resource.getInputStream()) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8.newDecoder()))) {
                StringBuilder builder = new StringBuilder();

                String line;
                while((line = reader.readLine()) != null) {
                    builder.append(line).append("\n");
                }

                return builder.toString();
            }
        }
    }

    public void attach(GlslProgram program) {
        IntConsumer consumer = program::attach;
        shaders.forEach(consumer);
    }

    public void detach(GlslProgram program) {
        IntConsumer consumer = program::detach;
        shaders.forEach(consumer);
    }

    @Override
    public void destroy() {
        IntPredicate predicate = shader -> {
            glDeleteShader(shader);
            return true;
        };
        shaders.removeIf(predicate);
    }
}
