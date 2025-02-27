package com.xiaohunao.equipment_benediction.common.hook.hooks;

import com.xiaohunao.equipment_benediction.common.context.AttackEntityContext;
import com.xiaohunao.equipment_benediction.common.hook.IHook;
import net.minecraft.world.entity.LivingEntity;

public interface LivingIncomingDamageHook extends IHook {
    boolean onLivingIncomingDamage(Object Owner, AttackEntityContext attackEntityContext);
}
