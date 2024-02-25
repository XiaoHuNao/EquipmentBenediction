package com.pancake.equipment_benediction.api;

import com.mojang.serialization.Codec;

public interface ICodec<T> {
    Codec<? extends T> codec();
    T type();
}