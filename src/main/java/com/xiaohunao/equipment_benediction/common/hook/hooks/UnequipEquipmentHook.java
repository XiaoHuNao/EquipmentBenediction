package com.xiaohunao.equipment_benediction.common.hook.hooks;

import com.xiaohunao.equipment_benediction.common.context.LivingEquipmentChangeContext;
import com.xiaohunao.equipment_benediction.common.hook.IHook;
import net.minecraft.world.entity.LivingEntity;

public interface UnequipEquipmentHook extends IHook {
    void onUnequipEquipment(Object Owner, LivingEquipmentChangeContext changeContext);
}
