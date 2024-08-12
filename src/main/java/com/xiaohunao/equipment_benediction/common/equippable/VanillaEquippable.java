package com.xiaohunao.equipment_benediction.common.equippable;

import com.xiaohunao.equipment_benediction.api.IEquippable;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.crafting.Ingredient;

public class VanillaEquippable implements IEquippable {
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof VanillaEquippable vanillaEquippable) {
            return this.slotType == vanillaEquippable.slotType;
        }
        return false;
    }
    @Override
    public int hashCode() {
        return slotType.hashCode();
    }
}
