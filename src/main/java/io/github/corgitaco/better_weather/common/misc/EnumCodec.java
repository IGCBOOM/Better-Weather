package io.github.corgitaco.better_weather.common.misc;

import com.mojang.serialization.Codec;
import net.minecraft.Util;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class EnumCodec<E extends Enum<E> & StringRepresentable> {
    private final @Unmodifiable Map<String, E> map;
    private final Codec<E> codec;

    public EnumCodec(E[] values) {
        map = Collections.unmodifiableMap(Util.make(new HashMap<>(), map -> {
            for (E e : values) {
                map.put(e.name(), e);
            }
        }));
        codec = StringRepresentable.fromEnum(() -> values, this::getValue);
    }

    @Nullable
    public E getValue(String key) {
        return map.get(key);
    }

    public boolean hasValue(String key) {
        return map.containsKey(key);
    }

    @NotNull
    public Codec<E> get() {
        return codec;
    }
}
