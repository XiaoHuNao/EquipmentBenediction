package com.xiaohunao.equipment_benediction.common.verifier;

import com.mojang.serialization.Codec;
import com.xiaohunao.equipment_benediction.common.codec.CodecProvider;
import com.xiaohunao.equipment_benediction.common.init.EQRegistries;
import net.minecraft.world.item.ItemStack;

import java.util.function.Function;

public interface IVerifier extends CodecProvider<IVerifier> {
    Codec<IVerifier> CODEC = Codec.lazyInitialized(() -> EQRegistries.Suppliers.VERIFIER_CODEC.get().byNameCodec()).dispatch(IVerifier::mapCodec, Function.identity());

    boolean verify(ItemStack stack);
}
