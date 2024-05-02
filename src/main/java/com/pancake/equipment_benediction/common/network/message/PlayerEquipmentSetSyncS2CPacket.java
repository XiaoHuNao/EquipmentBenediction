package com.pancake.equipment_benediction.common.network.message;

import com.pancake.equipment_benediction.api.IEquipmentSet;
import com.pancake.equipment_benediction.common.equipment_set.EquipmentSetHelper;
import com.pancake.equipment_benediction.common.init.ModEquipmentSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PlayerEquipmentSetSyncS2CPacket {
    private IEquipmentSet set;
    private boolean flag;
    public PlayerEquipmentSetSyncS2CPacket(IEquipmentSet set, boolean flag) {
        this.set = set;
        this.flag = flag;
    }


    public PlayerEquipmentSetSyncS2CPacket(FriendlyByteBuf buf){
        this.set = buf.readJsonWithCodec(ModEquipmentSet.REGISTRY.get().getCodec());
        this.flag = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buf){
        buf.writeJsonWithCodec(ModEquipmentSet.REGISTRY.get().getCodec(),set);
        buf.writeBoolean(flag);
    }

    public static void handle(PlayerEquipmentSetSyncS2CPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() ->
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> PlayerEquipmentSetSyncS2CPacket.ClientPacketHandlerClass.handlePacket(msg,ctx))
        );
        ctx.get().setPacketHandled(true);
    }

    public static class ClientPacketHandlerClass {
        public static void handlePacket(PlayerEquipmentSetSyncS2CPacket msg, Supplier<NetworkEvent.Context> ctx) {
            IEquipmentSet set = msg.set;
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null) {
                if (msg.flag) {
                    EquipmentSetHelper.addPlayerSet(set, player);
                } else {
                    EquipmentSetHelper.removePlayerSet(set, player);
                }
            }
        }
    }
}
