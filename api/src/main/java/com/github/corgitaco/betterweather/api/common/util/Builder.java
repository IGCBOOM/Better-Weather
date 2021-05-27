package com.github.corgitaco.betterweather.api.common.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface Builder<T> {

    @Contract("!null -> param1")
    @NotNull
    T build(T object);
}
