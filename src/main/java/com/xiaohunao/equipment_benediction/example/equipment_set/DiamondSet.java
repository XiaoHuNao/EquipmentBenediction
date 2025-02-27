package com.xiaohunao.equipment_benediction.example.equipment_set;

import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSet;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquippableGroup;
import com.xiaohunao.equipment_benediction.common.equipment_set.WearBonus;
import com.xiaohunao.equipment_benediction.common.equippable.VanillaEquippable;
import com.xiaohunao.equipment_benediction.common.hook.HookMap;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

public class DiamondSet extends EquipmentSet {
    @Override
    protected void init(HookMap.Builder hook, EquippableGroup.Builder equippableGroup, WearBonus.Builder wearBonus) {
        equippableGroup.addEquippable(
                VanillaEquippable.HEAD, Ingredient.of(Items.DIAMOND_HELMET),
                VanillaEquippable.CHEST, Ingredient.of(Items.DIAMOND_CHESTPLATE),
                VanillaEquippable.LEGS, Ingredient.of(Items.DIAMOND_LEGGINGS),
                VanillaEquippable.FEET, Ingredient.of(Items.DIAMOND_BOOTS)
        );

        wearBonus.addBonus(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN,-1,0));
    }
}
