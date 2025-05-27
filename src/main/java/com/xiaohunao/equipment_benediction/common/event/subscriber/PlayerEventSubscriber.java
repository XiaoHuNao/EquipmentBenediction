package com.xiaohunao.equipment_benediction.common.event.subscriber;

import com.xiaohunao.equipment_benediction.EquipmentBenediction;
import com.xiaohunao.equipment_benediction.common.attachment.EntityHookManager;
import com.xiaohunao.equipment_benediction.common.hook.HookMapManager;
import com.xiaohunao.equipment_benediction.common.init.EBAttachments;
import com.xiaohunao.equipment_benediction.common.init.EBHookTypes;
import com.xiaohunao.equipment_benediction.common.network.EntityHookManagerSyncPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.network.PacketDistributor;


@EventBusSubscriber
public class PlayerEventSubscriber {
    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        Player entity = event.getEntity();
        Player original = event.getOriginal();
        if (event.isWasDeath() && original.hasData(EBAttachments.ENTITY_HOOK_MANAGER)) {
            EntityHookManager entityHookManager = original.getData(EBAttachments.ENTITY_HOOK_MANAGER).updateActivatedHooks(original);
            entity.setData(EBAttachments.ENTITY_HOOK_MANAGER, entityHookManager);
        }
    }

    @SubscribeEvent
    public static void onPlayerInteractRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        InteractionHand hand = event.getHand();
        Player player = event.getEntity();
        if (hand != InteractionHand.MAIN_HAND) {
            return;
        }
        if (player.isShiftKeyDown() && !FMLEnvironment.production) {
            EntityHookManager data = player.getData(EBAttachments.ENTITY_HOOK_MANAGER);
            HookMapManager hookMapManager = HookMapManager.getInstance();
            EquipmentBenediction.LOGGER.debug("{}", data);
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer){
            PacketDistributor.sendToPlayer(serverPlayer, new EntityHookManagerSyncPayload(serverPlayer.getId(),serverPlayer.getData(EBAttachments.ENTITY_HOOK_MANAGER).serializeNBT(null)));
        }
    }


    @SubscribeEvent
    public static void onPlayerBreakSpeed(PlayerEvent.BreakSpeed event) {
        if (event.isCanceled()) return;
        HookMapManager.postHooks(EBHookTypes.BREAK_SPEED.get(), (owner, hook, original) -> {
            hook.onBreakSpeed(owner, original);
            return original;
        }, event.getEntity(), event);
    }
}