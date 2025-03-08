package com.xiaohunao.equipment_benediction.common.init;

import com.xiaohunao.equipment_benediction.EquipmentBenediction;
import com.xiaohunao.equipment_benediction.common.network.EntityHookManagerSyncPayload;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = EquipmentBenediction.MODID, bus = EventBusSubscriber.Bus.MOD)
public class EBNetworks {
    public static final String VERSION = "0.0.1";

    @SubscribeEvent
    public static void registerPayload(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(VERSION);
        registrar.playBidirectional(EntityHookManagerSyncPayload.TYPE, EntityHookManagerSyncPayload.STREAM_CODEC, new DirectionalPayloadHandler<>(
                EntityHookManagerSyncPayload::clientHandle,EntityHookManagerSyncPayload::serverHandle)
        );
    }
}