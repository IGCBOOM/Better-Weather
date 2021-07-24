package corgitaco.betterweather.api.client.opengl.glsl;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

@OnlyIn(Dist.CLIENT)
public class LazyGlslProgram {
    private final Supplier<GlslProgram> supplier;

    private GlslProgram program;

    public LazyGlslProgram(GlslCompilation compilation) {
        this.supplier = () -> GlslProgram.createFrom(compilation);
    }

    public GlslProgram create() {
        return program == null ? program = supplier.get() : program;
    }

    public GlslProgram get() {
        return requireNonNull(program, "Value does not exist!");
    }

    public void ifPresent(Consumer<GlslProgram> consumer) {
        if (program != null) {
            consumer.accept(program);
        }
    }
}
