package com.xiaohunao.equipment_benediction.common.hook.hooks;

import com.xiaohunao.equipment_benediction.common.context.AttackEntityContext;
import com.xiaohunao.equipment_benediction.common.hook.IHook;
import com.xiaohunao.equipment_benediction.common.interfaces.IBenediction;

public interface BeforeRangedHitHook extends IHook {
    void beforeRangedHit(IBenediction owner, AttackEntityContext context);
}
