package com.xiaohunao.equipment_benediction.common.init;

import com.xiaohunao.equipment_benediction.EquipmentBenediction;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSet;
import com.xiaohunao.equipment_benediction.api.EquipmentSetManager;
import com.xiaohunao.equipment_benediction.common.init.register.EBDeferredHolder;
import com.xiaohunao.equipment_benediction.common.init.register.EBDeferredRegister;
import com.xiaohunao.equipment_benediction.example.equipment_set.DiamondSet;

public class EBEquipmentSets {
    public static final EBDeferredRegister<EquipmentSet> EQUIPMENT_SET = EBDeferredRegister.create(
            EquipmentBenediction.MODID,
            EquipmentSetManager.getInstance()
    );

    public static final EBDeferredHolder<EquipmentSet> DIAMOND_SET = EQUIPMENT_SET.register("diamond_set", DiamondSet::new);
    public static final EBDeferredHolder<EquipmentSet> GOLD_SET = EQUIPMENT_SET.register("gold_set", DiamondSet::new);
}
