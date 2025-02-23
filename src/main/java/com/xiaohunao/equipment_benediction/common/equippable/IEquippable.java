package com.xiaohunao.equipment_benediction.common.equippable;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.crafting.Ingredient;

public interface IEquippable {
    boolean checkEquippable(LivingEntity player, Ingredient ingredient);
}
