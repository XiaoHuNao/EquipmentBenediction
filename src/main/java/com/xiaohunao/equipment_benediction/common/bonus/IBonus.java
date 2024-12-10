package com.xiaohunao.equipment_benediction.common.bonus;

import com.mojang.serialization.Codec;
import com.xiaohunao.equipment_benediction.common.codec.CodecProvider;
import com.xiaohunao.equipment_benediction.common.init.EQRegistries;
import com.xiaohunao.equipment_benediction.common.verifier.IVerifier;
import net.minecraft.world.entity.player.Player;

import java.util.function.Function;

public interface IBonus extends CodecProvider<IBonus> {
    Codec<IBonus> CODEC = Codec.lazyInitialized(() -> EQRegistries.Suppliers.BONUS_CODEC.get().byNameCodec()).dispatch(IBonus::mapCodec, Function.identity());


    void apply(Player player);

    void clear(Player player);
}
