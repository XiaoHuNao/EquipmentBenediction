package com.pancake.equipment_benediction.compat.curios.event.subscriber;

import com.pancake.equipment_benediction.common.modifier.ModifierHelper;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import top.theillusivec4.curios.api.event.CurioChangeEvent;

public class CurioPlayerEventSubscriber {
    @SubscribeEvent
    public static void onCurioChange(CurioChangeEvent event) {
        ItemStack from = event.getFrom();
        ItemStack to = event.getTo();
        LivingEntity entity = event.getEntity();

        if (entity.level().isClientSide) return;

        if (entity instanceof Player player){
            ListTag fromTag = ModifierHelper.getItemStackListTag(from);
            ListTag toTag = ModifierHelper.getItemStackListTag(to);
            ListTag playerTag = ModifierHelper.getPlayerListTag(player);


            fromTag.forEach((tag) -> {
                ModifierHelper.removePlayerModifierWithUpdate(player, tag);
            });

            ModifierHelper.addPlayerModifierWithUpdate(player, toTag);
        }
    }
}