package com.xiaohunao.equipment_benediction.common.event.subscriber;

import com.xiaohunao.equipment_benediction.common.context.LivingEquipmentChangeContext;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSetManager;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;

@EventBusSubscriber
public class CommonHook {
    @SubscribeEvent
    public static void onLivingEquipmentChange(LivingEquipmentChangeEvent event) {
        ItemStack from = event.getFrom();
        ItemStack to = event.getTo();
        EquipmentSlot slot = event.getSlot();
        LivingEntity livingEntity = event.getEntity();
        LivingEquipmentChangeContext livingEquipmentChangeContext = LivingEquipmentChangeContext.of(from, to, slot, livingEntity);

        if (!livingEquipmentChangeContext.from().isEmpty()) {

        }


        if (!livingEquipmentChangeContext.to().isEmpty()) {

        }
    }
}
