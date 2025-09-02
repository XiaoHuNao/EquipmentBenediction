package com.xiaohunao.equipment_benediction.common.equippable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.xiaohunao.equipment_benediction.common.init.EBRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Function;

public interface IWearable {
    Codec<IWearable> CODEC = Codec.lazyInitialized(() -> EBRegistries.Suppliers.EQUIPPABLE_CODEC.get().byNameCodec()).dispatch(IWearable::codec, Function.identity());

    boolean checkWearable(LivingEntity player, Ingredient ingredient);

    MapCodec<? extends IWearable> codec();

    ResourceLocation getIcon();
}
