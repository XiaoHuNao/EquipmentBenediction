package com.pancake.equipment_benediction.compat.curios.event.subscriber;

import com.pancake.equipment_benediction.common.equipment_set.EquipmentSetHelper;
import com.pancake.equipment_benediction.common.equippable.VanillaIEquippable;
import com.pancake.equipment_benediction.common.modifier.ModifierHelper;
import com.pancake.equipment_benediction.compat.curios.equippable.CurioEquippable;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.event.CurioChangeEvent;

public class CurioPlayerEventSubscriber {
    @SubscribeEvent
    public static void onCurioChange(CurioChangeEvent event) {
        ItemStack from = event.getFrom();
        ItemStack to = event.getTo();
        LivingEntity entity = event.getEntity();
        int slotIndex = event.getSlotIndex();
        if (entity.level().isClientSide) return;

        if (entity instanceof Player player){
            CuriosApi.getCuriosInventory(player).ifPresent(inventory -> {
                inventory.getCurios().forEach((identifier, stackHandler) -> {
                    ModifierHelper.updateModifier(from, to,CurioEquippable.of(stackHandler.getIdentifier()),player);
                    EquipmentSetHelper.updateSet(from, to, CurioEquippable.of(stackHandler.getIdentifier()),player);
                });
            });

        }
    }
}