package com.pancake.equipment_benediction.common.modifier;

import com.pancake.equipment_benediction.EquipmentBenediction;
import com.pancake.equipment_benediction.common.bonus.BonusHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;

import java.util.List;

public class MagneticModifier extends Modifier{
    public static final ResourceLocation IDENTIFIER = EquipmentBenediction.asResource("magnetic");

    @Override
    public void init(BonusHandler<ModifierInstance> handle) {
//        handle.addTick((player,modifierInstance) -> {
//            ModifierHelper.getPlayerModifierListTag(player).forEach((nbt) -> {
//                ModifierHelper.parse(nbt).ifPresent((instance) -> {
//                    if (instance.getModifier() instanceof MagneticModifier) {
//                        applyVelocity(player, instance.getAmplifier(), ItemEntity.class, 3, 0.05f, 100);
//                    }
//                });
//            });
//        });

        handle.addEvent(TickEvent.PlayerTickEvent.class, (event) -> {
            Player player = event.player;
            if (player.level().isClientSide()) return;


            ModifierHelper.getPlayerListTag(player).forEach((nbt) -> {
                ModifierHelper.parse(nbt).ifPresent((instance) -> {
                    if (instance.getModifier() instanceof MagneticModifier) {
                        applyVelocity(player, instance.getAmplifier(), ItemEntity.class, 3, 0.05f, 100);
                    }
                });
            });
        });

    }

    public static <T extends Entity> void applyVelocity(LivingEntity entity, int amplifier, Class<T> targetClass, int minRange, float speed, int maxPush) {
        double x = entity.getX();
        double y = entity.getY();
        double z = entity.getZ();
        float range = minRange + amplifier;
        List<T> targets = entity.level().getEntitiesOfClass(targetClass, new AABB(x - range,
                y - range,
                z - range,
                x + range,
                y + range,
                z + range));

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
