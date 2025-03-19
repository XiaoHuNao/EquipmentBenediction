package com.xiaohunao.equipment_benediction.common.hook.hooks;

import com.xiaohunao.equipment_benediction.common.context.AttackEntityContext;
import com.xiaohunao.equipment_benediction.common.context.DamageResultContainer;
import com.xiaohunao.equipment_benediction.common.hook.IHook;
import com.xiaohunao.equipment_benediction.common.interfaces.IBenediction;
import net.minecraft.world.entity.LivingEntity;

public interface LivingIncomingDamageHook extends IHook {
    DamageResultContainer onLivingIncomingDamage(IBenediction Owner, AttackEntityContext attackEntityContext);
}
