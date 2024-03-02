package com.pancake.equipment_benediction.common.equipment_set;

import com.pancake.equipment_benediction.api.IEquipmentSet;
import com.pancake.equipment_benediction.common.bonus.BonusHandler;
import com.pancake.equipment_benediction.common.equippable.EquippableGroup;
import com.pancake.equipment_benediction.common.equippable.VanillaIEquippable;
//import net.minecraft.world.item.Items;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.Tags;

public class DiamondSet extends EquipmentSet{
    @Override
    public void init(BonusHandler<IEquipmentSet> handler, EquippableGroup group) {
        group.addGroup(VanillaIEquippable.of("head"), Ingredient.of(Items.DIAMOND_HELMET))
                .addGroup(VanillaIEquippable.of("chest"), Ingredient.of(Items.DIAMOND_CHESTPLATE))
                .addGroup(VanillaIEquippable.of("legs"), Ingredient.of(Items.DIAMOND_LEGGINGS))
                .addGroup(VanillaIEquippable.of("feet"), Ingredient.of(Items.DIAMOND_BOOTS))
                .addGroup(VanillaIEquippable.of("feet"), Ingredient.of(Tags.Items.GLASS));
        handler.addTick((player,equipmentSet) -> {
            if (EquipmentSetHelper.hasSet(player, this)) {
            }
        });
    }
}
