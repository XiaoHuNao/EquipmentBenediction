package com.xiaohunao.equipment_benediction.common.context;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public record LivingEquipmentChangeContext(ItemStack from, ItemStack to, EquipmentSlot slot, LivingEntity livingEntity) {
    public static LivingEquipmentChangeContext of(ItemStack from, ItemStack to, EquipmentSlot slot, LivingEntity livingEntity) {
        return new LivingEquipmentChangeContext(from, to, slot, livingEntity);
    }
}
