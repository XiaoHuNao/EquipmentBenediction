package com.xiaohunao.equipment_benediction.common.event.subscriber;

import com.xiaohunao.equipment_benediction.api.manager.EquipmentSetManager;
import com.xiaohunao.equipment_benediction.common.context.AttackEntityContext;
import com.xiaohunao.equipment_benediction.common.context.LivingEquipmentChangeContext;
import com.xiaohunao.equipment_benediction.common.hook.HookMapManager;
import com.xiaohunao.equipment_benediction.common.init.EBHookTypes;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.event.entity.EntityInvulnerabilityCheckEvent;
import net.neoforged.neoforge.event.entity.living.*;

@EventBusSubscriber
public class CommonHook {
    @SubscribeEvent
    public static void onLivingEquipmentChange(LivingEquipmentChangeEvent event) {
        ItemStack from = event.getFrom();
        ItemStack to = event.getTo();
        EquipmentSlot slot = event.getSlot();
        LivingEntity livingEntity = event.getEntity();
        if (!(livingEntity instanceof Player)) {
            return;
        }
        LivingEquipmentChangeContext changeContext = LivingEquipmentChangeContext.of(from, to, slot, livingEntity);


        if (!changeContext.from().isEmpty()) {
            //卸下装备Hook
            HookMapManager.postHooks(EBHookTypes.UNEQUIP_EQUIPMENT.get(), (owner, hook, original) -> {
                hook.onUnequipEquipment(owner, changeContext);
                return null;
            }, livingEntity);
        }

        EquipmentSetManager.getInstance().updateSet(changeContext);

        if (!changeContext.to().isEmpty()) {
            //装备Hook
            HookMapManager.postHooks(EBHookTypes.EQUIP_EQUIPMENT.get(), (owner, hook, original) -> {
                hook.onEquipEquipment(owner, changeContext);
                return null;
            }, livingEntity);
        }
    }

    @SubscribeEvent
    public static void onMobEffectApplicable(MobEffectEvent.Applicable event) {
        if (event.getResult() != MobEffectEvent.Applicable.Result.DEFAULT) return;
        HookMapManager.postHooks(EBHookTypes.MOB_EFFECT_APPLICABLE.get(), (owner, hook, original) -> {
            hook.onMobEffectApplicable(owner, original);
            return original;
        }, event.getEntity(), event);
    }

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.Pre event) {
        DamageContainer container = event.getContainer();
        DamageSource damageSource = container.getSource();
        Entity attacker = damageSource.getEntity();
        if (attacker == null) return;
        if (damageSource.is(DamageTypeTags.IS_PLAYER_ATTACK)) {
            AttackEntityContext context = AttackEntityContext.of(damageSource.getEntity(), event.getEntity(), container, damageSource.getWeaponItem());
            //近战攻击命中Hook
            HookMapManager.postHooks(EBHookTypes.BEFORE_MELEE_HIT.get(), (owner, hook, original) -> {
                hook.beforeMeleeHit(owner, context);
                return null;
            }, attacker);
        }

        HookMapManager.postHooks(EBHookTypes.BEFORE_LIVING_DAMAGE.get(), (owner, hook, original) -> {
            hook.beforeLivingDamage(owner, original);
            return original;
        }, attacker, event);
    }

    @SubscribeEvent
    public static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        if (event.isCanceled()) return;
        HookMapManager.postHooks(EBHookTypes.LIVING_INCOMING_DAMAGE.get(), (owner, hook, original) -> {
            hook.onLivingIncomingDamage(owner, original);
            return original;
        }, event.getEntity(), event);
    }

    @SubscribeEvent
    public static void onLivingHeal(LivingHealEvent event) {
        if (event.isCanceled()) return;
        HookMapManager.postHooks(EBHookTypes.LIVING_HEAL.get(), (owner, hook, original) -> {
            hook.onLivingHeal(owner, original);
            return original;
        }, event.getEntity(), event);
    }

    @SubscribeEvent
    public static void onLivingBreathe(LivingBreatheEvent event) {
        if (event.canBreathe()) return;
        HookMapManager.postHooks(EBHookTypes.LIVING_BREATHE.get(), (owner, hook, original) -> {
            hook.onLivingBreathe(owner, original);
            return original;
        }, event.getEntity(), event);
    }

    @SubscribeEvent
    public static void onLivingShieldBlock(LivingShieldBlockEvent event) {
        if (event.isCanceled()) return;
        HookMapManager.postHooks(EBHookTypes.LIVING_SHIELD_BLOCK.get(), (owner, hook, original) -> {
            hook.onLivingShieldBlock(owner, original);
            return original;
        }, event.getEntity(), event);
    }

    @SubscribeEvent
    public static void onEntityInvulnerabilityCheck(EntityInvulnerabilityCheckEvent event) {
        if (event.isInvulnerable()) return;
        HookMapManager.postHooks(EBHookTypes.ENTITY_INVULNERABILITY_CHECK.get(), (owner, hook, original) -> {
            hook.onEntityInvulnerabilityCheck(owner, original);
            return original;
        }, event.getEntity(), event);
    }
}
