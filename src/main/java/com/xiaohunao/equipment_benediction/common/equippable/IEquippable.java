package com.xiaohunao.equipment_benediction.common.equippable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.JsonOps;
import com.xiaohunao.equipment_benediction.common.init.EBRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Function;

public interface IEquippable {
    Codec<IEquippable> CODEC = Codec.lazyInitialized(() -> EBRegistries.Suppliers.EQUIPPABLE_CODEC.get().byNameCodec()).dispatch(IEquippable::codec, Function.identity());

    boolean checkEquippable(LivingEntity player, Ingredient ingredient);

    MapCodec<? extends IEquippable> codec();

}
