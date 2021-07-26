package corgitaco.betterweather.api.client.opengl.glsl;

import corgitaco.betterweather.BetterWeather;
import corgitaco.betterweather.api.client.opengl.Destroyable;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.function.Supplier;

import static org.lwjgl.opengl.GL20.*;

@OnlyIn(Dist.CLIENT)
public class GlslProgram implements Destroyable {
    private final int program = glCreateProgram();

    private final Object2IntMap<String> uniforms = new Object2IntOpenHashMap<>();

    private final GlslSources sources;

    private GlslProgram(GlslSources sources) {
        this.sources = sources;

        Supplier<String> infoLog = () -> {
            try (MemoryStack stack = MemoryStack.stackPush()) {

                IntBuffer lengthBuffer = stack.mallocInt(1);
                glGetProgramiv(program, GL_INFO_LOG_LENGTH, lengthBuffer);

                int length = lengthBuffer.get();

                return glGetProgramInfoLog(program, length);
            }
        };

        sources.attach(this);

        glLinkProgram(program);
        if (glGetProgrami(program, GL_LINK_STATUS) == GL_FALSE) {
            BetterWeather.LOGGER.error(infoLog.get());

            destroy();
        }

        sources.detach(this);

        glValidateProgram(program);
        if (glGetProgrami(program, GL_VALIDATE_STATUS) == GL_FALSE) {
            BetterWeather.LOGGER.warn(infoLog.get());
        }
    }

    public void upload1i(String location, int i) {
        glUniform1i(compute(location), i);
    }

    public void upload4f(String location, float x, float y, float z, float w) {
        glUniform4f(compute(location), x, y, z, w);
    }

    public void uploadMatrix4f(String location, Matrix4f matrix4f) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(16);
            matrix4f.write(buffer);
            buffer.rewind();

            glUniformMatrix4fv(compute(location), false, buffer);
        }
    }

    private int compute(String location) {
        return uniforms.computeIfAbsent(location, key -> glGetUniformLocation(program, location));
    }

    public void attach(int shader) {
        glAttachShader(program, shader);
    }

    public void detach(int shader) {
        glAttachShader(program, shader);
    }

    public void bind() {
        glUseProgram(program);
    }

    public static void unbind() {
        glUseProgram(0);
    }

    @Override
    public void destroy() {
        uniforms.clear();
        sources.destroy();
        glDeleteProgram(program);
    }

    public static @NotNull GlslProgram createFrom(GlslCompilation compilation) {
        GlslSources sources = new GlslSources();
        try {
            compilation.compile(sources);
        } catch (IOException e) {
            BetterWeather.LOGGER.error(e);
            sources.destroy();
        }
        return new GlslProgram(sources);
    }
}
