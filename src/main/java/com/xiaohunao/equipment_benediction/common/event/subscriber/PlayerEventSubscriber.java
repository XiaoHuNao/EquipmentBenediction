package com.xiaohunao.equipment_benediction.common.event.subscriber;

import com.xiaohunao.equipment_benediction.EquipmentBenediction;
import com.xiaohunao.equipment_benediction.api.IModifier;
import com.xiaohunao.equipment_benediction.common.capability.LastInventoryCap;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSetHelper;
import com.xiaohunao.equipment_benediction.common.equipment_set.equippable.VanillaIEquippable;
import com.xiaohunao.equipment_benediction.common.event.PlayerContainerChangeEvent;
import com.xiaohunao.equipment_benediction.common.modifier.ModifierHelper;
import com.xiaohunao.equipment_benediction.common.quality.QualityHelper;
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
    public static void onLivingEquipmentChange(PlayerContainerChangeEvent event) {
        LivingEntity entity = event.getEntity();
        EquipmentSlot slot = event.getSlot();
        ItemStack from = event.getFrom();
        ItemStack to = event.getTo();


        if (!(entity instanceof Player player)) return;


        QualityHelper.updateQuality(to);
        ModifierHelper.updateModifier(from, to,player);
        EquipmentSetHelper.updateSet(from, to,player);
    }

    @SubscribeEvent
    public static void onTickPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Player player = event.player;
            if (player.level.isClientSide()) return;

            LastInventoryCap.get(player).ifPresent(LastInventoryCap::update);
            LastInventoryCap.get(player).ifPresent(LastInventoryCap::updateInventory);
            ModifierHelper.getPlayerListTag(player).forEach((tag) -> {
                ModifierHelper.parse(tag).ifPresent((instance) -> {
                    IModifier modifier = instance.getModifier();
                    if (modifier != null) {
                        modifier.getHandler().getTickBonus().accept(player, instance);
                    }
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
                        if (instance.getModifier().getGroup().checkBlacklist(VanillaIEquippable.of(EquipmentSlot.OFFHAND))) {
                            event.setCanceled(true);
                        }
                    });
        });
    }

}
