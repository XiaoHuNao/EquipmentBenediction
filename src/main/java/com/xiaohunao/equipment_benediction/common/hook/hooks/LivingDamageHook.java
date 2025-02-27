package com.xiaohunao.equipment_benediction.common.hook.hooks;

import com.xiaohunao.equipment_benediction.common.context.AttackEntityContext;
import com.xiaohunao.equipment_benediction.common.hook.IHook;

public interface LivingDamageHook extends IHook {
    float onLivingDamage(Object Owner, AttackEntityContext attackEntityContext);
}
