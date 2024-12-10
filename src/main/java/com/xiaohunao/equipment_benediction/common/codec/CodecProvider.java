package com.xiaohunao.equipment_benediction.common.codec;

import com.mojang.serialization.MapCodec;

public interface CodecProvider<T> {
    MapCodec<? extends T> mapCodec();
}