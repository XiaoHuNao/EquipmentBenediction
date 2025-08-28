package com.xiaohunao.equipment_benediction.common.hook.hooks;

import com.xiaohunao.equipment_benediction.common.context.AttackEntityContext;
import com.xiaohunao.equipment_benediction.common.hook.IHook;
import com.xiaohunao.equipment_benediction.common.interfaces.IBenediction;

@FunctionalInterface
public interface BeforeMeleeHitHook extends IHook {
    void beforeMeleeHit(IBenediction Owner, AttackEntityContext attackEntityContext);
}
