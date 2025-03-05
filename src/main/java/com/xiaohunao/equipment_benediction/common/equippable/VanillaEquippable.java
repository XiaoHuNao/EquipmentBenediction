package com.xiaohunao.equipment_benediction.common.equippable;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.crafting.Ingredient;

public class VanillaEquippable implements IEquippable {
    public static final VanillaEquippable
            MAINHAND = new VanillaEquippable(EquipmentSlot.MAINHAND),
            OFFHAND = new VanillaEquippable(EquipmentSlot.OFFHAND),
            HEAD = new VanillaEquippable(EquipmentSlot.HEAD),
            CHEST = new VanillaEquippable(EquipmentSlot.CHEST),
            LEGS = new VanillaEquippable(EquipmentSlot.LEGS),
            FEET = new VanillaEquippable(EquipmentSlot.FEET);

    public static final VanillaEquippable[] VALUES = new VanillaEquippable[]{
            MAINHAND, OFFHAND, HEAD, CHEST, LEGS, FEET
    };

    private final EquipmentSlot slotType;

    public VanillaEquippable(EquipmentSlot slotType) {
        this.slotType = slotType;
    }

    @Override
    public boolean checkEquippable(LivingEntity livingEntity, Ingredient ingredient) {
        return ingredient.test(livingEntity.getItemBySlot(slotType));
    }

    public static VanillaEquippable of(String slotType) {
        return new VanillaEquippable(EquipmentSlot.byName(slotType));
    }

    public EquipmentSlot getSlotType() {
        return slotType;
    }

}