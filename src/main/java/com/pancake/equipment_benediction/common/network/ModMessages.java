package com.pancake.equipment_benediction.common.network;

import com.pancake.equipment_benediction.EquipmentBenediction;
import com.pancake.equipment_benediction.common.network.message.ItemStackModifierSyncS2CPacket;
import com.pancake.equipment_benediction.common.network.message.PlayerModifierSyncS2CPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import org.lwjgl.system.windows.MSG;

public class ModMessages {
    private static SimpleChannel INSTANCE;

    private static int packetId = 0;
    private static int id(){
        return packetId++;
    }

    public static void register(){
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(EquipmentBenediction.asResource("messages"))
                .networkProtocolVersion(()->"1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        INSTANCE = net;


        net.messageBuilder(ItemStackModifierSyncS2CPacket.class,id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(ItemStackModifierSyncS2CPacket::new)
                .encoder(ItemStackModifierSyncS2CPacket::toBytes)
                .consumerMainThread(ItemStackModifierSyncS2CPacket::handle)
                .add();
        net.messageBuilder(PlayerModifierSyncS2CPacket.class,id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(PlayerModifierSyncS2CPacket::new)
                .encoder(PlayerModifierSyncS2CPacket::toBytes)
                .consumerMainThread(PlayerModifierSyncS2CPacket::handle)
                .add();
    }

    public static <MSG> void sendToServer(MSG message){
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToClient(MSG message, ServerPlayer player){
        INSTANCE.send(PacketDistributor.PLAYER.with(()-> player),message);
    }

}