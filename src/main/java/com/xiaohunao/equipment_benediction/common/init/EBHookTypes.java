package com.xiaohunao.equipment_benediction.common.init;

import com.mojang.serialization.MapCodec;
import com.xiaohunao.equipment_benediction.EquipmentBenediction;
import com.xiaohunao.equipment_benediction.common.hook.HookType;
import com.xiaohunao.equipment_benediction.common.hook.IHook;
import com.xiaohunao.equipment_benediction.common.hook.dynamic.*;
import com.xiaohunao.equipment_benediction.common.hook.hooks.*;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EBHookTypes {
    public static final DeferredRegister<HookType<?>> HOOK_TYPES = DeferredRegister.create(EBRegistries.Keys.HOOK_TYPES, EquipmentBenediction.MODID);

    public static final DeferredHolder<HookType<?>, HookType<BeforeMeleeHitHook>> BEFORE_MELEE_HIT = register("before_melee_hit", BeforeMeleeHitHook.class);
    public static final DeferredHolder<HookType<?>, HookType<BeforeLivingDamageHook>> BEFORE_LIVING_DAMAGE = register("before_living_damage", BeforeLivingDamageHook.class);
    public static final DeferredHolder<HookType<?>, HookType<EquipEquipmentHook>> EQUIP_EQUIPMENT = register("equip_equipment", EquipEquipmentHook.class);
    public static final DeferredHolder<HookType<?>, HookType<UnequipEquipmentHook>> UNEQUIP_EQUIPMENT = register("unequip_equipment", UnequipEquipmentHook.class);
    public static final DeferredHolder<HookType<?>, HookType<LivingIncomingDamageHook>> LIVING_INCOMING_DAMAGE = register("living_incoming_damage", LivingIncomingDamageHook.class);
    public static final DeferredHolder<HookType<?>, HookType<MobEffectApplicableHook>> MOB_EFFECT_APPLICABLE = register("mob_effect_applicable", MobEffectApplicableHook.class);
    public static final DeferredHolder<HookType<?>, HookType<BreakSpeedHook>> BREAK_SPEED = register("break_speed", BreakSpeedHook.class);
    public static final DeferredHolder<HookType<?>, HookType<LivingHealHook>> LIVING_HEAL = register("living_heal", LivingHealHook.class);
    public static final DeferredHolder<HookType<?>, HookType<LivingBreatheHook>> LIVING_BREATHE = register("living_breathe", LivingBreatheHook.class);
    public static final DeferredHolder<HookType<?>, HookType<LivingShieldBlockHook>> LIVING_SHIELD_BLOCK = register("living_shield_block", LivingShieldBlockHook.class);
    public static final DeferredHolder<HookType<?>, HookType<EntityInvulnerabilityCheckHook>> ENTITY_INVULNERABILITY_CHECK = register("entity_invulnerability_check", EntityInvulnerabilityCheckHook.class);
    public static final DeferredHolder<HookType<?>, HookType<LivingGetProjectileHook>> LIVING_GET_PROJECTILE = register("living_get_projectile", LivingGetProjectileHook.class);
    public static final DeferredHolder<HookType<?>, HookType<PlayerTickHook>> PLAYER_TICK = register("player_tick", PlayerTickHook.class);


    public static final DeferredHolder<HookType<?>, HookType<KnockbackHook>> KNOCKBACK = registerSerializable("knockback", KnockbackHook.class, KnockbackHook.CODEC);
    public static final DeferredHolder<HookType<?>, HookType<MobEffectImmunityHook>> MOB_EFFECT_IMMUNITY = registerSerializable("mob_effect_immunity", MobEffectImmunityHook.class, MobEffectImmunityHook.CODEC);
    public static final DeferredHolder<HookType<?>, HookType<DamageTypeImmunityHook>> DAMAGE_TYPE_IMMUNITY = registerSerializable("damage_type_immunity", DamageTypeImmunityHook.class, DamageTypeImmunityHook.CODEC);

    private static <T extends IHook> DeferredHolder<HookType<?>, HookType<T>> register(String id, Class<T> hookClass) {
        return HOOK_TYPES.register(id, () -> HookType.createHook(EquipmentBenediction.asResource(id), hookClass));
    }

    private static <T extends ISerializableHook> DeferredHolder<HookType<?>, HookType<T>> registerSerializable(String id, Class<T> hookClass, MapCodec<T> codec) {
        return HOOK_TYPES.register(id, () -> HookType.createSerializableHook(EquipmentBenediction.asResource(id), hookClass, codec));
    }
}
