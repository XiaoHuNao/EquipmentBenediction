package com.pancake.equipment_benediction.common.event.subscriber;

import com.pancake.equipment_benediction.EquipmentBenediction;
import com.pancake.equipment_benediction.common.capability.LastInventoryCap;
import com.pancake.equipment_benediction.common.event.PlayerEquipmentChangeEvent;
import com.pancake.equipment_benediction.common.modifier.ModifierHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(modid = EquipmentBenediction.MOD_ID)
public class PlayerEventSubscriber {
    @SubscribeEvent
    public static void onLivingEquipmentChange(PlayerEquipmentChangeEvent event) {
        LivingEntity entity = event.getEntity();
        EquipmentSlot slot = event.getSlot();
        ItemStack from = event.getFrom();
        ItemStack to = event.getTo();


        if (!(entity instanceof Player player) || player.level().isClientSide()) return;

        ModifierHelper.updateModifier(from, to, slot,player);
    }

    @SubscribeEvent
    public static void onTickPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Player player = event.player;
            if (player.level().isClientSide()) return;

            LastInventoryCap.get(player).ifPresent(LastInventoryCap::update);
            ModifierHelper.updateTickModifiers(player);
        }
    }

}
