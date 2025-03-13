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
    Codec<IEquippable> CODEC = ResourceLocation.CODEC.dispatch(
        equippable -> {
            ResourceLocation id = EBRegistries.Suppliers.EQUIPPABLE_CODEC.get().getKey(equippable.codec());
            if (id == null) {
                throw new IllegalStateException("Equippable " + equippable + " is not registered");
            }
            return id;
        },
        id -> {
            MapCodec<? extends IEquippable> codec = EBRegistries.Suppliers.EQUIPPABLE_CODEC.get().get(id);
            if (codec == null) {
                throw new IllegalStateException("No codec registered for " + id);
            }
            return codec;
        }
    );

    boolean checkEquippable(LivingEntity player, Ingredient ingredient);

    MapCodec<? extends IEquippable> codec();

    default String getMapKey() {
        ResourceLocation id = EBRegistries.Suppliers.EQUIPPABLE_CODEC.get().getKey(codec());
        return id != null ? id.toString() : toString();
    }
}
