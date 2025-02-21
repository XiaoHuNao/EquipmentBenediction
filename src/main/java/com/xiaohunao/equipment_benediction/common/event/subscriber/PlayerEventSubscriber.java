package com.xiaohunao.equipment_benediction.common.event.subscriber;

import com.xiaohunao.equipment_benediction.common.init.EBModifiers;
import com.xiaohunao.equipment_benediction.common.init.EBRegistries;
import com.xiaohunao.equipment_benediction.common.manager.ModifierManager;
import com.xiaohunao.equipment_benediction.common.modifier.Modifier;
import com.xiaohunao.equipment_benediction.common.modifier.SerializableModifier;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.Map;


@EventBusSubscriber
public class PlayerEventSubscriber {
    @SubscribeEvent
    public static void onPlayerInteractRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        InteractionHand hand = event.getHand();
        Player player = event.getEntity();
        if (level.isClientSide() || hand != InteractionHand.MAIN_HAND) {
            return;
        }

//        Registry<Modifier> modifiers = level.registryAccess().registryOrThrow(EBRegistries.Keys.MODIFIERS);
//        Modifier modifier = modifiers.get(EBModifiers.Dynamic.KNOCKBACK);
//        System.out.println(modifier);
//        Holder.Reference<Modifier> orThrow = modifierRegistryLookup.getOrThrow(EBModifiers.Dynamic.KNOCKBACK);
//        Holder<Modifier> delegate = orThrow.getDelegate();
//        Modifier value = delegate.value();
//        System.out.println(value);

        ModifierManager instance = ModifierManager.getInstance();
        Map<ResourceLocation,Modifier> modifiers = instance.getAllResources();
        System.out.println(modifiers);
    }

}