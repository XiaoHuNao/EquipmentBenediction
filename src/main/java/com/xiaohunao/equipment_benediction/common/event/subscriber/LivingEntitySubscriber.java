package com.xiaohunao.equipment_benediction.common.event.subscriber;


import com.xiaohunao.equipment_benediction.common.context.AttackEntityContext;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        LOGGER.info("{} {} {} {}",entity1,directEntity,entity,source);

        if(source.is(DamageTypeTags.IS_PLAYER_ATTACK)){
            AttackEntityContext attackEntityContext = AttackEntityContext.of(source.getEntity(),entity, container,source.getWeaponItem());
        }
        if (source.is(DamageTypeTags.IS_PROJECTILE)) {
            LOGGER.info("projectile attack");
        }
        if (source.is(Tags.DamageTypes.IS_MAGIC)){
            LOGGER.info("magic attack");
        }
    }
}
