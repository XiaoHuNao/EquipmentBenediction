package com.xiaohunao.equipment_benediction.common.event.subscriber;

import com.xiaohunao.equipment_benediction.EquipmentBenediction;
import com.xiaohunao.equipment_benediction.api.manager.EquipmentSetManager;
import com.xiaohunao.equipment_benediction.common.context.AttackEntityContext;
import com.xiaohunao.equipment_benediction.common.context.LivingEquipmentChangeContext;
import com.xiaohunao.equipment_benediction.common.hook.HookMapManager;
import com.xiaohunao.equipment_benediction.common.hook.hooks.AfterLivingHurtEntityHook;
import com.xiaohunao.equipment_benediction.common.init.EBAttachments;
import com.xiaohunao.equipment_benediction.common.init.EBHookTypes;
import com.xiaohunao.equipment_benediction.common.mixed.ILivingEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.event.entity.EntityInvulnerabilityCheckEvent;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = EquipmentBenediction.MODID, bus = EventBusSubscriber.Bus.GAME)
public class CommonHook {
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Pre event) {
        Player player = event.getEntity();
        HookMapManager.postHooks(EBHookTypes.PLAYER_TICK.get(), (owner, hook, original) -> {
            hook.onPlayerTick(owner, original);
            return original;
        }, player, player);

        player.getData(EBAttachments.ENTITY_HOOK_MANAGER).tickSpecialTimeHook(player);
    }


    @SubscribeEvent
    public static void onLivingEquipmentChange(LivingEquipmentChangeEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player) || !((ILivingEntity) player).equipment_benediction$isFirstSynced()) return;
        ItemStack from = event.getFrom();
        ItemStack to = event.getTo();
        EquipmentSlot slot = event.getSlot();
        LivingEquipmentChangeContext changeContext = LivingEquipmentChangeContext.of(from, to, slot, player);


        if (!changeContext.from().isEmpty()) {
            // 卸下装备Hook
            HookMapManager.postHooks(EBHookTypes.UNEQUIP_EQUIPMENT.get(), (owner, hook, original) -> {
                hook.onUnequipEquipment(owner, original);
                return original;
            }, player, changeContext);
        }

        EquipmentSetManager.getInstance().updateSet(player, changeContext);

        if (!changeContext.to().isEmpty()) {
            // 装备Hook
            HookMapManager.postHooks(EBHookTypes.EQUIP_EQUIPMENT.get(), (owner, hook, original) -> {
                hook.onEquipEquipment(owner, original);
                return original;
            }, player, changeContext);
        }
    }

    @SubscribeEvent
    public static void onMobEffectApplicable(MobEffectEvent.Applicable event) {
        if (event.getResult() != MobEffectEvent.Applicable.Result.DEFAULT || !(event.getEntity() instanceof Player player)) return;
        HookMapManager.postHooks(EBHookTypes.MOB_EFFECT_APPLICABLE.get(), (owner, hook, original) -> {
            hook.onMobEffectApplicable(owner, original);
            return original;
        }, player, event);
    }

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.Pre event) {
        DamageContainer container = event.getContainer();
        DamageSource damageSource = container.getSource();
        Entity attacker = damageSource.getEntity();
        if (!(attacker instanceof Player)) return;
        if (damageSource.is(DamageTypeTags.IS_PLAYER_ATTACK)) {
            AttackEntityContext context = AttackEntityContext.of(damageSource.getEntity(), event.getEntity(), container, damageSource.getWeaponItem());
            //近战攻击命中Hook
            HookMapManager.postHooks(EBHookTypes.BEFORE_MELEE_HIT.get(), (owner, hook, original) -> {
                hook.beforeMeleeHit(owner, original);
                return original;
            }, attacker, context);
        }

        HookMapManager.postHooks(EBHookTypes.BEFORE_LIVING_DAMAGE.get(), (owner, hook, original) -> {
            hook.beforeLivingDamage(owner, original);
            return original;
        }, attacker, event);
    }

    @SubscribeEvent
    public static void afterLivingDamage(LivingDamageEvent.Post event) {
        if (event.getSource().getEntity() instanceof Player attacker) {
            HookMapManager.postHooks(EBHookTypes.AFTER_LIVING_HURT_ENTITY.get(), (owner, hook, original) -> {
                hook.afterLivingHurtEntity(owner, original);
                return original;
            }, attacker, new AfterLivingHurtEntityHook.Data(attacker, event.getEntity(), event.getSource(), event.getNewDamage()));
        }
    }

    @SubscribeEvent
    public static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        HookMapManager.postHooks(EBHookTypes.LIVING_INCOMING_DAMAGE.get(), (owner, hook, original) -> {
            hook.onLivingIncomingDamage(owner, original);
            return original;
        }, player, event);
    }

    @SubscribeEvent
    public static void onLivingHeal(LivingHealEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        HookMapManager.postHooks(EBHookTypes.LIVING_HEAL.get(), (owner, hook, original) -> {
            hook.onLivingHeal(owner, original);
            return original;
        }, player, event);
    }

    @SubscribeEvent
    public static void onLivingBreathe(LivingBreatheEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        HookMapManager.postHooks(EBHookTypes.LIVING_BREATHE.get(), (owner, hook, original) -> {
            hook.onLivingBreathe(owner, original);
            return original;
        }, player, event);
    }

    @SubscribeEvent
    public static void onLivingShieldBlock(LivingShieldBlockEvent event) {
        if (!event.getBlocked() || !(event.getEntity() instanceof Player player)) return;
        HookMapManager.postHooks(EBHookTypes.LIVING_SHIELD_BLOCK.get(), (owner, hook, original) -> {
            hook.onLivingShieldBlock(owner, event);
            return original;
        }, player, event);
    }

    @SubscribeEvent
    public static void onEntityInvulnerabilityCheck(EntityInvulnerabilityCheckEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        HookMapManager.postHooks(EBHookTypes.ENTITY_INVULNERABILITY_CHECK.get(), (owner, hook, original) -> {
            hook.onEntityInvulnerabilityCheck(owner, original);
            return original;
        }, player, event);
    }

    @SubscribeEvent
    public static void livingGetProjectile(LivingGetProjectileEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        HookMapManager.postHooks(EBHookTypes.LIVING_GET_PROJECTILE.get(), (owner, hook, original) -> {
            hook.onLivingGetProjectile(owner, original);
            return original;
        }, player, event);
    }
}
