package com.xiaohunao.equipment_benediction.common.event.subscriber;


import com.xiaohunao.equipment_benediction.common.component.ModifierComponent;
import com.xiaohunao.equipment_benediction.common.context.AttackEntityContext;
import com.xiaohunao.equipment_benediction.common.hook.hooks.BeforeMeleeHitHook;
import com.xiaohunao.equipment_benediction.common.init.EBDataComponentTypes;
import com.xiaohunao.equipment_benediction.common.init.EBHookTypes;
import com.xiaohunao.equipment_benediction.common.modifier.ModifierInstance;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

@EventBusSubscriber
public class LivingEntitySubscriber {
    private static final Logger LOGGER = LoggerFactory.getLogger(LivingEntitySubscriber.class);

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.Pre event) {
        LivingEntity entity = event.getEntity();
        DamageContainer container = event.getContainer();
        DamageSource source = container.getSource();
        Entity entity1 = source.getEntity();
        Entity directEntity = source.getDirectEntity();
        ItemStack weaponItem = source.getWeaponItem();
        LOGGER.info("{} {} {} {}",entity1,directEntity,entity,source);

        if(source.is(DamageTypeTags.IS_PLAYER_ATTACK)){
            if (weaponItem != null) {
                AttackEntityContext attackEntityContext = AttackEntityContext.of(source.getEntity(),entity, container, weaponItem);
                ModifierComponent modifierComponent = weaponItem.getOrDefault(EBDataComponentTypes.MODIFIER, ModifierComponent.EMPTY);
                for (ModifierInstance modifierInstance : modifierComponent.modifierInstances()) {
                    Collection<BeforeMeleeHitHook> beforeMeleeHitHooks = modifierInstance.getModifier().getHookMap().get(EBHookTypes.BEFORE_MELEE_HIT);
                    for (BeforeMeleeHitHook beforeMeleeHitHook : beforeMeleeHitHooks) {
                        beforeMeleeHitHook.beforeMeleeHit(modifierInstance, attackEntityContext);
                    }
                }
            }
        }
        if (source.is(DamageTypeTags.IS_PROJECTILE)) {
            LOGGER.info("projectile attack");
        }
        if (source.is(Tags.DamageTypes.IS_MAGIC)){
            LOGGER.info("magic attack");
        }
    }
}
