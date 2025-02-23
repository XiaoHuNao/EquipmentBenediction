package com.xiaohunao.equipment_benediction.common.hook.hooks;

import com.xiaohunao.equipment_benediction.common.context.AttackEntityContext;
import com.xiaohunao.equipment_benediction.common.hook.IHook;
import com.xiaohunao.equipment_benediction.common.modifier.ModifierInstance;

@FunctionalInterface
public interface BeforeMeleeHitHook extends IHook {

    void beforeMeleeHit(AttackEntityContext attackEntityContext);
}
