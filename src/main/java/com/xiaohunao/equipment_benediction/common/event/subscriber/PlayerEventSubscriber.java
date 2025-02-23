package com.xiaohunao.equipment_benediction.common.event.subscriber;

import com.xiaohunao.equipment_benediction.EquipmentBenediction;
import com.xiaohunao.equipment_benediction.common.component.ModifierComponent;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSet;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSetManager;
import com.xiaohunao.equipment_benediction.common.init.EBDataComponentTypes;
import com.xiaohunao.equipment_benediction.common.init.EBModifiers;
import com.xiaohunao.equipment_benediction.common.init.EBRegistries;
import com.xiaohunao.equipment_benediction.common.manager.ModifierManager;
import com.xiaohunao.equipment_benediction.common.modifier.Modifier;
import com.xiaohunao.equipment_benediction.common.modifier.ModifierInstance;
import com.xiaohunao.equipment_benediction.common.modifier.SerializableModifier;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.List;
import java.util.Map;


@EventBusSubscriber
public class PlayerEventSubscriber {
    @SubscribeEvent
    public static void onPlayerInteractRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        InteractionHand hand = event.getHand();
        Player player = event.getEntity();
        if (level.isClientSide() || hand != InteractionHand.MAIN_HAND) {
            return;
        }

//        if (player.isShiftKeyDown()){
//            ItemStack mainHandItem = player.getMainHandItem();
//            mainHandItem.set(EBDataComponentTypes.MODIFIER.get(),new ModifierComponent(List.of(new ModifierInstance(EquipmentBenediction.asResource("knockback"),1))));
//        }

//        EquipmentSetManager instance = EquipmentSetManager.getInstance();
//        Map<ResourceLocation, EquipmentSet> allResources = instance.getAllResources();
//        System.out.println(allResources);
    }

//    @SubscribeEvent
//    public void onLivingEquipmentChange(LivingEquipmentChangeEvent event) {
//    }
//
//    @SubscribeEvent
//    public void onMobEffect(MobEffectEvent.Applicable event) {
////        Entity effectSource = event.getEffectSource();
////        event.setResult(MobEffectEvent.Applicable.Result.DEFAULT);
//    }
//
//    @SubscribeEvent
//    public void onLivingDamage(LivingDamageEvent.Pre event) {
//
//    }



}