package com.xiaohunao.equipment_benediction.common.hook.hooks;

import com.xiaohunao.equipment_benediction.common.context.LivingEquipmentChangeContext;
import com.xiaohunao.equipment_benediction.common.hook.IHook;
import com.xiaohunao.equipment_benediction.common.interfaces.IBenediction;

public interface EquipEquipmentHook extends IHook {
    void onEquipEquipment(IBenediction Owner, LivingEquipmentChangeContext changeContext);
}
