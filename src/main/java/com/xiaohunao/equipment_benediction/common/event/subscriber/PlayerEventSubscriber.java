package com.xiaohunao.equipment_benediction.common.event.subscriber;

import com.google.common.collect.BiMap;
import com.xiaohunao.equipment_benediction.api.manager.BenedictionManager;
import com.xiaohunao.equipment_benediction.common.interfaces.IBenediction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;


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

        if (player.isShiftKeyDown()){
//            ItemStack mainHandItem = player.getMainHandItem();
//            mainHandItem.set(EBDataComponentTypes.MODIFIER.get(),new ModifierComponent(List.of(new ModifierInstance(EquipmentBenediction.asResource("magnetic"),1))));

            player.getActiveEffectsMap().forEach((effect, effectInstance) -> {
                System.out.println(effect);
                System.out.println(effectInstance);
            });

            BenedictionManager instance = BenedictionManager.getInstance();
            BiMap<ResourceLocation, IBenediction<?>> allBenedictions = instance.getAllBenedictions();
            System.out.println(allBenedictions);
        }

    }



}