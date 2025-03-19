package com.xiaohunao.equipment_benediction.common.utils;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Tuple;

import java.util.List;
import java.util.Map;

public class CodecUtils {
    public static <A, B> Codec<Tuple<A, B>> tupleCodec(Codec<A> aCodec, Codec<B> bCodec) {
        return RecordCodecBuilder.create(instance -> instance.group(
                aCodec.fieldOf("key").forGetter(Tuple::getA),
                bCodec.fieldOf("value").forGetter(Tuple::getB)
        ).apply(instance, Tuple::new));
    }

    public static <K, V> Codec<Map<K, V>> tupleToMap(Codec<K> keyCodec, Codec<V> valueCodec) {
        return Codec.list(tupleCodec(keyCodec, valueCodec))
            .xmap(
                tuples -> {
                    Map<K, V> map = Maps.newHashMap();
                    tuples.forEach(tuple -> map.put(tuple.getA(), tuple.getB()));
                    return map;
                },
                // Map -> List<Tuple>
                map -> map.entrySet().stream()
                    .map(entry -> new Tuple<>(entry.getKey(), entry.getValue()))
                    .toList()
            );
    }
}
