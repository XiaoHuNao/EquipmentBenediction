package com.pancake.equipment_benediction.common.network.message;

import com.pancake.equipment_benediction.common.modifier.ModifierHelper;
import com.pancake.equipment_benediction.common.modifier.ModifierInstance;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PlayerModifierSyncS2CPacket {
    private ModifierInstance instance;
    private boolean flag;
    public PlayerModifierSyncS2CPacket(ModifierInstance instance,boolean flag) {
        this.instance = instance;
        this.flag = flag;
    }


    public PlayerModifierSyncS2CPacket(FriendlyByteBuf buf){
        this.instance = buf.readJsonWithCodec(ModifierInstance.CODEC);
        this.flag = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buf){
        buf.writeJsonWithCodec(ModifierInstance.CODEC,instance);
        buf.writeBoolean(flag);
    }

    public static void handle(PlayerModifierSyncS2CPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() ->
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandlerClass.handlePacket(msg,ctx))
        );
        ctx.get().setPacketHandled(true);
    }

    public static class ClientPacketHandlerClass {
        public static void handlePacket(PlayerModifierSyncS2CPacket msg, Supplier<NetworkEvent.Context> ctx) {
            ModifierInstance instance = msg.instance;
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null) {
                if (msg.flag) {
                    ModifierHelper.addPlayerModifier(instance, player);
                } else {
                    ModifierHelper.removePlayerModifier(instance, player);
                }
            }
        }
    }
}
