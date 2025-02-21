package com.xiaohunao.equipment_benediction.common.init;

import com.xiaohunao.equipment_benediction.EquipmentBenediction;
import com.xiaohunao.equipment_benediction.common.init.register.EBDeferredHolder;
import com.xiaohunao.equipment_benediction.common.init.register.EBDeferredRegister;
import com.xiaohunao.equipment_benediction.common.manager.ModifierManager;
import com.xiaohunao.equipment_benediction.common.modifier.Modifier;
import com.xiaohunao.equipment_benediction.example.modifier.MagneticModifier;

public class EBModifiers {
    public static final EBDeferredRegister<Modifier> MODIFIERS = EBDeferredRegister.create(
        EquipmentBenediction.MODID, 
        ModifierManager.getInstance()
    );

    // 静态修饰器 - 只注册一次
    public static final EBDeferredHolder<Modifier> MAGNETIC = MODIFIERS.register("magnetic", MagneticModifier::new);
    
    // 动态修饰器
    public static final EBDeferredHolder<Modifier> KNOCKBACK = MODIFIERS.registerDynamic("knockback");

    // 添加初始化方法
    public static void init() {
        // 空方法，仅用于确保类被加载
    }
}
