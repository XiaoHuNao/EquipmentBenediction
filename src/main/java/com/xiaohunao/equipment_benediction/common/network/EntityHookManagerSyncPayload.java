package com.xiaohunao.equipment_benediction.common.network;

import com.xiaohunao.equipment_benediction.EquipmentBenediction;
import com.xiaohunao.equipment_benediction.common.attachment.EntityHookManager;
import com.xiaohunao.equipment_benediction.common.hook.HookMap;
import com.xiaohunao.equipment_benediction.common.init.EBAttachments;
import com.xiaohunao.equipment_benediction.common.interfaces.IBenediction;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;



public record EntityHookManagerSyncPayload(Integer entityId, CompoundTag entityHookManager) implements CustomPacketPayload {
    public static final Type<EntityHookManagerSyncPayload> TYPE = new Type<>(EquipmentBenediction.asResource("entity_hook_manager_sync"));
    public static final StreamCodec<ByteBuf, EntityHookManagerSyncPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, EntityHookManagerSyncPayload::entityId,
            ByteBufCodecs.fromCodec(CompoundTag.CODEC), EntityHookManagerSyncPayload::entityHookManager,
            EntityHookManagerSyncPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void clientHandle(IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player().isLocalPlayer()) {
                updateEntity(context);
            }
        });
    }

    public void serverHandle(IPayloadContext payloadContext) {
        payloadContext.enqueueWork(() -> {
            if (!payloadContext.player().isLocalPlayer()) {
                updateEntity(payloadContext);
            }
        });
    }

    private void updateEntity(IPayloadContext payloadContext) {
        Level level = payloadContext.player().level();
        Entity entity = level.getEntity(entityId);
        if (entity != null) {
            EntityHookManager entityHookManager1 = new EntityHookManager();
            entityHookManager1.deserializeNBT(null, entityHookManager);
            entity.setData(EBAttachments.ENTITY_HOOK_MANAGER.get(), entityHookManager1);
        }else {
            EquipmentBenediction.LOGGER.error("EntityHookManagerSyncPayload: entity is null, entityId: {}", entityId);
        }
    }
}
