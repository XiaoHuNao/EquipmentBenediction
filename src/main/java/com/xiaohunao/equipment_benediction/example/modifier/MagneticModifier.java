package com.xiaohunao.equipment_benediction.example.modifier;

import com.xiaohunao.equipment_benediction.common.hook.HookMap;
import com.xiaohunao.equipment_benediction.common.init.EBHookTypes;
import com.xiaohunao.equipment_benediction.common.modifier.Modifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class MagneticModifier extends Modifier {
    @Override
    protected void init(HookMap.Builder hookBuilder) {
        hookBuilder.addHook(EBHookTypes.BEFORE_MELEE_HIT.get(), (owner, attackEntityContext) -> applyVelocity(attackEntityContext.attackerEntity(), 1, ItemEntity.class, 3, 0.5f, 100));
    }

    public static <T extends Entity> void applyVelocity(Entity entity, int amplifier, Class<T> targetClass, int minRange, float speed, int maxPush) {
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();
        float range = minRange + amplifier;
        List<T> targets = entity.level().getEntitiesOfClass(targetClass, new AABB(x - range, y - range, z - range, x + range, y + range, z + range));

        int pulled = 0;
        for (T target : targets) {
            if (target.isRemoved()) {
                continue;
            }

            Vec3 vec = entity.position()
                    .subtract(target.getX(), target.getY(), target.getZ())
                    .normalize()
                    .scale(speed * (amplifier + 1));
            if (!target.isNoGravity()) {
                vec = vec.add(0, 0.04f, 0);
            }

            target.setDeltaMovement(target.getDeltaMovement().add(vec));

            pulled++;
            if (pulled > maxPush) {
                break;
            }
        }
    }
}
