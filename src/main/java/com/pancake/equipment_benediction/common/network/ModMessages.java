package com.pancake.equipment_benediction.common.network;

import com.pancake.equipment_benediction.EquipmentBenediction;
import com.pancake.equipment_benediction.common.network.message.PlayerEquipmentSetSyncS2CPacket;
import com.pancake.equipment_benediction.common.network.message.PlayerModifierSyncS2CPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

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


        net.messageBuilder(PlayerModifierSyncS2CPacket.class,id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(PlayerModifierSyncS2CPacket::new)
                .encoder(PlayerModifierSyncS2CPacket::toBytes)
                .consumerMainThread(PlayerModifierSyncS2CPacket::handle)
                .add();

        net.messageBuilder(PlayerEquipmentSetSyncS2CPacket.class,id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(PlayerEquipmentSetSyncS2CPacket::new)
                .encoder(PlayerEquipmentSetSyncS2CPacket::toBytes)
                .consumerMainThread(PlayerEquipmentSetSyncS2CPacket::handle)
                .add();
    }

    public static <MSG> void sendToServer(MSG message){
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToClient(MSG message, ServerPlayer player){
        INSTANCE.send(PacketDistributor.PLAYER.with(()-> player),message);
    }

}