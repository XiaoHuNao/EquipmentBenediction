package com.xiaohunao.equipment_benediction.common.event.subscriber;

import com.xiaohunao.equipment_benediction.api.manager.EquipmentSetManager;
import com.xiaohunao.equipment_benediction.common.context.AttackEntityContext;
import com.xiaohunao.equipment_benediction.common.context.DamageResultContainer;
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
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber
public class CommonHook {
    @SubscribeEvent
    public static void onLivingEquipmentChange(LivingEquipmentChangeEvent event) {
        ItemStack from = event.getFrom();
        ItemStack to = event.getTo();
        EquipmentSlot slot = event.getSlot();
        LivingEntity livingEntity = event.getEntity();
        if (!(livingEntity instanceof Player)){
            return;
        }
        LivingEquipmentChangeContext changeContext = LivingEquipmentChangeContext.of(from, to, slot, livingEntity);


        if (!changeContext.from().isEmpty()) {
            //卸下装备Hook
            HookMapManager.postHooks(EBHookTypes.UNEQUIP_EQUIPMENT.get(), (owner, hook) -> {
                hook.onUnequipEquipment(owner, changeContext);
                return null;
            }, livingEntity);
        }

        EquipmentSetManager.getInstance().updateSet(changeContext);

        if (!changeContext.to().isEmpty()) {
            //装备Hook
            HookMapManager.postHooks(EBHookTypes.EQUIP_EQUIPMENT.get(), (owner, hook) -> {
                hook.onEquipEquipment(owner, changeContext);
                return null;
            }, livingEntity);
        }

    }

    @SubscribeEvent
    public static void onMobEffectApplicable(MobEffectEvent.Applicable event) {
        //药水应用Hook
        MobEffectEvent.Applicable.Result result = HookMapManager.postHooks(EBHookTypes.MOB_EFFECT_APPLICABLE.get(), (owner, hook) -> hook.onMobEffectApplicable(owner, event.getEntity(), event.getEffectInstance()), event.getEntity());
        if (result == null){
            return;
        }

        if (result != MobEffectEvent.Applicable.Result.DEFAULT){
            event.setResult(result);
        }
    }

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.Pre event) {
        LivingEntity entity = event.getEntity();
        DamageContainer container = event.getContainer();
        DamageSource source = container.getSource();
        Entity entity1 = source.getEntity();
        Entity directEntity = source.getDirectEntity();
        ItemStack weaponItem = source.getWeaponItem();
//        LOGGER.info("{} {} {} {}", entity1, directEntity, entity, source);
        if (source.is(DamageTypeTags.IS_PLAYER_ATTACK) && weaponItem != null) {
            AttackEntityContext attackEntityContext = AttackEntityContext.of(source.getEntity(), entity, container, weaponItem);
            //近战攻击命中Hook
            HookMapManager.postHooks(EBHookTypes.BEFORE_MELEE_HIT.get(), (owner, hook) -> {
                hook.beforeMeleeHit(owner, attackEntityContext);
                return null;
            },directEntity);
        }
//
//        if (source.is(DamageTypeTags.IS_PROJECTILE)) {
//            LOGGER.info("projectile attack");
//        }
//        if (source.is(Tags.DamageTypes.IS_MAGIC)) {
//            LOGGER.info("magic attack");
//        }
    }

    @SubscribeEvent
    public static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        LivingEntity entity = event.getEntity();
        DamageContainer container = event.getContainer();
        DamageSource source = container.getSource();
        Entity directEntity = source.getDirectEntity();
        ItemStack weaponItem = source.getWeaponItem();
        AttackEntityContext attackEntityContext = new AttackEntityContext(directEntity, entity, container, weaponItem);
        //传入伤害Hook
        DamageResultContainer damageResultContainer = HookMapManager.postHooks(EBHookTypes.LIVING_INCOMING_DAMAGE.get(), (owner, hook) -> hook.onLivingIncomingDamage(owner, attackEntityContext), event.getEntity());

        if (damageResultContainer == null){
            damageResultContainer = DamageResultContainer.empty();
        }

        damageResultContainer.InvulnerabilityTick().ifPresent(event::setInvulnerabilityTicks);
        damageResultContainer.damage().ifPresent(event::setAmount);
//        damageResultContainer.reduction().ifPresent(event::addReductionModifier);
        damageResultContainer.isCanceled().ifPresent(event::setCanceled);

    }

    @SubscribeEvent
    public static void onPlayerBreakSpeed(PlayerEvent.BreakSpeed event) {
        float originalSpeed = event.getOriginalSpeed();
        Float newSpeed = HookMapManager.postHooks(EBHookTypes.BREAK_SPEED.get(), (owner, hook) -> hook.onBreakSpeed(owner, event.getEntity(), event.getState(), originalSpeed), event.getEntity());
        if (newSpeed != null && newSpeed != originalSpeed){
            event.setNewSpeed(newSpeed);
        }
    }


}
