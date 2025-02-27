package com.xiaohunao.equipment_benediction.common.event.subscriber;

import com.xiaohunao.equipment_benediction.common.context.LivingEquipmentChangeContext;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSetManager;
import com.xiaohunao.equipment_benediction.common.hook.HookMapManager;
import com.xiaohunao.equipment_benediction.common.init.EBHookTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
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
        if (!(livingEntity instanceof Player)){
            return;
        }
        LivingEquipmentChangeContext changeContext = LivingEquipmentChangeContext.of(from, to, slot, livingEntity);


        //这个顺序不能随便改,除非你知道你在做什么
        if (!changeContext.from().isEmpty()) {
            HookMapManager.postHooks(EBHookTypes.UNEQUIP_EQUIPMENT.get(), (owner,hook) -> hook.onUnequipEquipment(owner, changeContext), livingEntity);
        }

        EquipmentSetManager.getInstance().updateSet(changeContext);

        if (!changeContext.to().isEmpty()) {
            HookMapManager.postHooks(EBHookTypes.EQUIP_EQUIPMENT.get(), (owner,hook) -> hook.onEquipEquipment(owner, changeContext), livingEntity);
        }

    }
}
