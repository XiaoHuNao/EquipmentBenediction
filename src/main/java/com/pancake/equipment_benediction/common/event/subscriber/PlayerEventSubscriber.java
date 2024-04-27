package com.pancake.equipment_benediction.common.event.subscriber;

import com.pancake.equipment_benediction.EquipmentBenediction;
import com.pancake.equipment_benediction.common.capability.LastInventoryCap;
import com.pancake.equipment_benediction.common.equipment_set.EquipmentSetHelper;
import com.pancake.equipment_benediction.common.equippable.VanillaIEquippable;
import com.pancake.equipment_benediction.common.event.PlayerEquipmentChangeEvent;
import com.pancake.equipment_benediction.common.modifier.ModifierHelper;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
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

        ModifierHelper.updateModifier(from, to,VanillaIEquippable.of(slot),player);
        EquipmentSetHelper.updateSet(from, to,VanillaIEquippable.of(slot),player);
    }

    @SubscribeEvent
    public static void onTickPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Player player = event.player;
            if (player.level().isClientSide()) return;

            LastInventoryCap.get(player).ifPresent(LastInventoryCap::update);
            ModifierHelper.getPlayerListTag(player).forEach((tag) -> {
                ModifierHelper.parse(tag).ifPresent((instance) -> {
                    instance.getModifier().getHandler().getTickBonus().accept(player,instance);
                });
            });


            EquipmentSetHelper.getPlayerListTag(player).forEach((tag) -> {
                EquipmentSetHelper.parse(tag).ifPresent((set) -> {
                    set.getHandler().getTickBonus().accept(player,set);
                });
            });
        }
    }
    @SubscribeEvent
    public static void checkOffhandModifierBlacklist(PlayerInteractEvent event) {
        Level level = event.getLevel();
        InteractionHand hand = event.getHand();
        Player player = event.getEntity();
        if (level.isClientSide() || hand == InteractionHand.MAIN_HAND) {
            return;
        }
        ModifierHelper.getPlayerListTag(player).forEach((nbt) -> {
            ModifierHelper.parse(nbt)
                    .ifPresent((instance) -> {
                        if (instance.getModifier().getGroup().checkBlacklist(VanillaIEquippable.of("offhand"))) {
                            event.setCanceled(true);
                        }
                    });
        });
    }
}
