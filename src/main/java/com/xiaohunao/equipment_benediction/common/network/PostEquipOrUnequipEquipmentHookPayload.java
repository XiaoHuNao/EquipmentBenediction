package com.xiaohunao.equipment_benediction.common.network;

import com.xiaohunao.equipment_benediction.EquipmentBenediction;
import com.xiaohunao.equipment_benediction.common.context.LivingEquipmentChangeContext;
import com.xiaohunao.equipment_benediction.common.hook.HookMapManager;
import com.xiaohunao.equipment_benediction.common.init.EBHookTypes;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PostEquipOrUnequipEquipmentHookPayload(boolean isEquip) implements CustomPacketPayload{
    public static final CustomPacketPayload.Type<PostEquipOrUnequipEquipmentHookPayload> TYPE = new CustomPacketPayload.Type<>(EquipmentBenediction.asResource("post_equip_or_unequip_equipment_hook"));
    public static final StreamCodec<ByteBuf, PostEquipOrUnequipEquipmentHookPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, PostEquipOrUnequipEquipmentHookPayload::isEquip,
            PostEquipOrUnequipEquipmentHookPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void serverHandle(IPayloadContext payloadContext) {
        payloadContext.enqueueWork(() -> {
            Player player = payloadContext.player();
            if (!player.isLocalPlayer()) {
                if (isEquip){
                    HookMapManager.postHooks(EBHookTypes.EQUIP_EQUIPMENT.get(), (owner, hook) -> {
                        hook.onEquipEquipment(owner, new LivingEquipmentChangeContext(null,null,null,player));
                        return null;
                    }, player);
                }else {
                    HookMapManager.postHooks(EBHookTypes.UNEQUIP_EQUIPMENT.get(), (owner, hook) -> {
                        hook.onUnequipEquipment(owner, new LivingEquipmentChangeContext(null,null,null,player));
                        return null;
                    }, player);
                }
            }
        });
    }
}
