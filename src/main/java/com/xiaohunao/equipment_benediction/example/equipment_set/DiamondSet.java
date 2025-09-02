package com.xiaohunao.equipment_benediction.example.equipment_set;

import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSet;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSetBranch;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquippableGroup;
import com.xiaohunao.equipment_benediction.common.equippable.VanillaWearable;
import com.xiaohunao.equipment_benediction.common.hook.HookMap;
import com.xiaohunao.equipment_benediction.common.init.EBHookTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.fml.loading.FMLEnvironment;

public class DiamondSet extends EquipmentSet {
    @Override
    protected void init(HookMap.Builder hook, EquippableGroup.Builder equippableGroup) {
        if(FMLEnvironment.production){
            return;
        }


        EquipmentSetBranch setData1 = new EquipmentSetBranch.Builder()
                .addEquippable(
                        VanillaWearable.HEAD , Ingredient.of(Items.DIAMOND_HELMET),
                        VanillaWearable.CHEST, Ingredient.of(Items.DIAMOND_CHESTPLATE),
                        VanillaWearable.LEGS , Ingredient.of(Items.DIAMOND_LEGGINGS),
                        VanillaWearable.FEET , Ingredient.of(Items.DIAMOND_BOOTS)
                )
                .bindHook(wearBonusHook -> wearBonusHook
                        .addBonus(new MobEffectInstance(MobEffects.MOVEMENT_SPEED,-1,0))
                )
                .build();

        EquipmentSetBranch setData2 = new EquipmentSetBranch.Builder()
                .addEquippable(
                        VanillaWearable.FEET , Ingredient.of(Items.DIAMOND_BOOTS)
                )
                .bindHook(wearBonusHook -> wearBonusHook
                        .addMobEffectImmunityBonus(MobEffects.MOVEMENT_SLOWDOWN)
                )
                .build();


        EquipmentSetBranch setData3 =  new EquipmentSetBranch.Builder()
                .addEquippable(
                        VanillaWearable.HEAD , Ingredient.of(Items.DIAMOND_HELMET),
                        VanillaWearable.CHEST, Ingredient.of(Items.DIAMOND_CHESTPLATE),
                        VanillaWearable.LEGS , Ingredient.of(Items.DIAMOND_LEGGINGS),
                        VanillaWearable.FEET , Ingredient.of(Items.DIAMOND_BOOTS)
                ).setRequiredMatchCount(2)
                .bindHook(wearBonusHook -> wearBonusHook
                        .addDamageTypeImmunity(DamageTypeTags.IS_PROJECTILE)
                )
                .build();

        EquipmentSetBranch setData4 =  new EquipmentSetBranch.Builder()
                .addEquippable(
                        VanillaWearable.HEAD , Ingredient.of(Items.DIAMOND_HELMET),
                        VanillaWearable.FEET , Ingredient.of(Items.DIAMOND_BOOTS)
                )
                .bindHook(wearBonusHook -> wearBonusHook
                        .addBonus(Attributes.MOVEMENT_SPEED,new AttributeModifier(ResourceLocation.tryBuild("equipment_benediction","diamond_set_4"),2, AttributeModifier.Operation.ADD_VALUE))
                )
                .build();

        EquipmentSetBranch setData5 =  new EquipmentSetBranch.Builder()
                .addEquippable(
                        VanillaWearable.HEAD , Ingredient.of(Items.DIAMOND_HELMET),
                        VanillaWearable.CHEST, Ingredient.of(Items.DIAMOND_CHESTPLATE),
                        VanillaWearable.LEGS , Ingredient.of(Items.DIAMOND_LEGGINGS),
                        VanillaWearable.FEET , Ingredient.of(Items.DIAMOND_BOOTS),
                        VanillaWearable.MAINHAND , Ingredient.of(Items.DIAMOND_SWORD)
                )
                .bindHook(EBHookTypes.BEFORE_MELEE_HIT.get(), (Owner, attackEntityContext) -> {
                    LivingEntity livingEntity = attackEntityContext.hitEntity();
                    livingEntity.setPos(livingEntity.getX(),livingEntity.getY() + 10,livingEntity.getZ());
                })
                .build();





        equippableGroup
                .addEquippableSet("111",setData1,true)
                .addEquippableSet("222",setData2)
                .addEquippableSet("333",setData3)
                .addEquippableSet("444",setData4,true)
                .addEquippableSet("555",setData5,true);
    }
}
