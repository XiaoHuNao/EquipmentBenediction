package com.xiaohunao.equipment_benediction.common.event.subscriber;

import com.xiaohunao.equipment_benediction.api.manager.EquipmentSetManager;
import com.xiaohunao.equipment_benediction.common.context.LivingEquipmentChangeContext;
import com.xiaohunao.equipment_benediction.common.hook.HookMapManager;
import com.xiaohunao.equipment_benediction.common.init.EBHookTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;

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


        if (!changeContext.from().isEmpty()) {
            HookMapManager.postHooks(EBHookTypes.UNEQUIP_EQUIPMENT.get(), (owner, hook) -> {
                hook.onUnequipEquipment(owner, changeContext);
                return null;
            }, livingEntity);
        }

        EquipmentSetManager.getInstance().updateSet(changeContext);

        if (!changeContext.to().isEmpty()) {
            HookMapManager.postHooks(EBHookTypes.EQUIP_EQUIPMENT.get(), (owner, hook) -> {
                hook.onEquipEquipment(owner, changeContext);
                return null;
            }, livingEntity);
        }

    }

    @SubscribeEvent
    public static void onMobEffectApplicable(MobEffectEvent.Applicable event) {
        MobEffectEvent.Applicable.Result result = HookMapManager.postHooks(EBHookTypes.MOB_EFFECT_APPLICABLE.get(), (owner, hook) -> hook.onMobEffectApplicable(owner, event.getEntity(), event.getEffectInstance()), event.getEntity());
        if (result == null){
            return;
        }

        if (result != MobEffectEvent.Applicable.Result.DEFAULT){
            event.setResult(result);
        }
    }

}
