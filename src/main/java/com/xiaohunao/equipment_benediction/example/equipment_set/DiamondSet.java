package com.xiaohunao.equipment_benediction.example.equipment_set;

import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSet;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquippableGroup;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquippableSetData;
import com.xiaohunao.equipment_benediction.common.equippable.VanillaEquippable;
import com.xiaohunao.equipment_benediction.common.hook.HookMap;
import com.xiaohunao.equipment_benediction.common.init.EBHookTypes;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.Tags;

public class DiamondSet extends EquipmentSet {
    @Override
    protected void init(HookMap.Builder hook, EquippableGroup.Builder equippableGroup) {
        if(FMLEnvironment.production){
            return;
        }


        EquippableSetData setData1 = new EquippableSetData.Builder()
                .addEquippable(
                        VanillaEquippable.HEAD , Ingredient.of(Items.DIAMOND_HELMET),
                        VanillaEquippable.CHEST, Ingredient.of(Items.DIAMOND_CHESTPLATE),
                        VanillaEquippable.LEGS , Ingredient.of(Items.DIAMOND_LEGGINGS),
                        VanillaEquippable.FEET , Ingredient.of(Items.DIAMOND_BOOTS)
                )
                .bindHook(wearBonusHook -> wearBonusHook
                        .addBonus(new MobEffectInstance(MobEffects.MOVEMENT_SPEED,-1,0))
                )
                .build();

        EquippableSetData setData2 = new EquippableSetData.Builder()
                .addEquippable(
                        VanillaEquippable.FEET , Ingredient.of(Items.DIAMOND_BOOTS)
                )
                .bindHook(wearBonusHook -> wearBonusHook
                        .addMobEffectImmunityBonus(MobEffects.MOVEMENT_SLOWDOWN)
                )
                .build();


        EquippableSetData setData3 =  new EquippableSetData.Builder()
                .addEquippable(
                        VanillaEquippable.HEAD , Ingredient.of(Items.DIAMOND_HELMET),
                        VanillaEquippable.CHEST, Ingredient.of(Items.DIAMOND_CHESTPLATE),
                        VanillaEquippable.LEGS , Ingredient.of(Items.DIAMOND_LEGGINGS),
                        VanillaEquippable.FEET , Ingredient.of(Items.DIAMOND_BOOTS)
                ).setRequiredMatchCount(2)
                .bindHook(wearBonusHook -> wearBonusHook
                        .addDamageTypeImmunity(DamageTypeTags.IS_PROJECTILE)
                )
                .build();

        EquippableSetData setData4 =  new EquippableSetData.Builder()
                .addEquippable(
                        VanillaEquippable.HEAD , Ingredient.of(Items.DIAMOND_HELMET),
                        VanillaEquippable.CHEST, Ingredient.of(Items.DIAMOND_CHESTPLATE),
                        VanillaEquippable.LEGS , Ingredient.of(Items.DIAMOND_LEGGINGS),
                        VanillaEquippable.FEET , Ingredient.of(Items.DIAMOND_BOOTS),
                        VanillaEquippable.MAINHAND , Ingredient.of(Items.DIAMOND_SWORD)
                )
                .bindHook(EBHookTypes.BEFORE_MELEE_HIT.get(), (Owner, attackEntityContext) -> {
                    LivingEntity livingEntity = attackEntityContext.hitEntity();
                    livingEntity.setPos(livingEntity.getX(),livingEntity.getY() + 10,livingEntity.getZ());
                })
                .build();





        equippableGroup
                .addEquippableSet(setData1,true)
                .addEquippableSet(setData2)
                .addEquippableSet(setData3)
                .addEquippableSet(setData4,true);
    }
}
