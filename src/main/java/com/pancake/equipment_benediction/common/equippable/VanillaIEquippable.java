package com.pancake.equipment_benediction.common.equippable;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Ingredient;

public class VanillaIEquippable extends Equippable<EquipmentSlot> {
    public VanillaIEquippable(EquipmentSlot slotType) {
        super(slotType);
    }

    @Override
    public boolean checkEquippable(Player player, Ingredient ingredient) {
        return ingredient.test(player.getItemBySlot(slotType));
    }

    public static VanillaIEquippable of(String slotType) {
        return new VanillaIEquippable(EquipmentSlot.byName(slotType));
    }

}
