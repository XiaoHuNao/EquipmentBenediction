package com.pancake.equipment_benediction.common.network.message;

import com.pancake.equipment_benediction.common.modifier.ModifierInstance;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ItemStackModifierSyncS2CPacket {
    private ItemStack stack;
    private ModifierInstance instance;
    private boolean flag;
    public ItemStackModifierSyncS2CPacket(ItemStack stack,ModifierInstance instance,boolean flag) {
        this.stack = stack;
        this.instance = instance;
        this.flag = flag;
    }


    public ItemStackModifierSyncS2CPacket(FriendlyByteBuf buf){
        this.stack = buf.readItem();
        this.instance = buf.readJsonWithCodec(ModifierInstance.CODEC);
        this.flag = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buf){
        buf.writeItem(stack);
        buf.writeJsonWithCodec(ModifierInstance.CODEC,instance);
        buf.writeBoolean(flag);
    }

    public static void handle(ItemStackModifierSyncS2CPacket msg,Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() ->
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientPacketHandlerClass.handlePacket(msg,ctx))
        );
        ctx.get().setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    public static class ClientPacketHandlerClass {
        public static void handlePacket(ItemStackModifierSyncS2CPacket msg,Supplier<NetworkEvent.Context> ctx) {
            ModifierInstance instance = msg.instance;
            ItemStack stack = msg.stack;
//            ModifierCap.get(stack).ifPresent((cap) -> {
//                if(msg.flag){
//                    cap.addModifier(instance);
//                }
//                else {
//                    cap.removeModifier(instance.getModifier());
//                }
//
//
//            });
        }
    }

}
