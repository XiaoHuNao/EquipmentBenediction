package com.xiaohunao.equipment_benediction.common.hook.hooks;

import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSet;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSetBranch;
import com.xiaohunao.equipment_benediction.common.hook.IHook;
import net.minecraft.world.entity.LivingEntity;

public interface SuppressionSetBranchHook extends IHook {

    void onSuppressionSetBranch(LivingEntity livingEntity, EquipmentSet equipmentSet, EquipmentSetBranch setBranch);
}
