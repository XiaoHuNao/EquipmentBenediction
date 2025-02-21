package com.xiaohunao.equipment_benediction.common.context;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.damagesource.DamageContainer;

public record AttackEntityContext(Entity attackerEntity, LivingEntity hitEntity, DamageContainer damageContainer, ItemStack hitItemStack) {
    public static AttackEntityContext of(Entity attackerEntity,LivingEntity hitEntity, DamageContainer damageContainer, ItemStack weapon) {
        return new AttackEntityContext(attackerEntity,hitEntity, damageContainer,weapon);
    }
}
