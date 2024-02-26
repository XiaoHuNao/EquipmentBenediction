package com.pancake.equipment_benediction.compat.curios.event.subscriber;

import com.pancake.equipment_benediction.EquipmentBenediction;
import com.pancake.equipment_benediction.common.modifier.ModifierHelper;
import com.pancake.equipment_benediction.common.network.ModMessages;
import com.pancake.equipment_benediction.common.network.message.PlayerModifierSyncS2CPacket;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.event.CurioChangeEvent;


@Mod.EventBusSubscriber(modid = EquipmentBenediction.MOD_ID)
public class PlayerEventSubscriber {
    @SubscribeEvent
    public static void onCurioChange(CurioChangeEvent event) {
        ItemStack from = event.getFrom();
        ItemStack to = event.getTo();
        LivingEntity entity = event.getEntity();

        if (entity.level().isClientSide) return;

        if (entity instanceof Player player){
            ListTag fromTag = ModifierHelper.getItemStackModifierListTag(from);
            ListTag toTag = ModifierHelper.getItemStackModifierListTag(to);
            ListTag playerTag = ModifierHelper.getPlayerModifierListTag(player);


            fromTag.forEach((tag) -> {
                ModifierHelper.removePlayerModifierWithUpdate(player, tag);
            });

            ModifierHelper.addPlayerModifierWithUpdate(player, toTag);
        }
    }
}