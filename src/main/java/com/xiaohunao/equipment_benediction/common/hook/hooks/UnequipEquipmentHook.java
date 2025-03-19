package com.xiaohunao.equipment_benediction.common.hook.hooks;

import com.xiaohunao.equipment_benediction.common.context.LivingEquipmentChangeContext;
import com.xiaohunao.equipment_benediction.common.hook.IHook;
import com.xiaohunao.equipment_benediction.common.interfaces.IBenediction;
import net.minecraft.world.entity.LivingEntity;

public interface UnequipEquipmentHook extends IHook {
    void onUnequipEquipment(IBenediction Owner, LivingEquipmentChangeContext changeContext);
}
