package com.pancake.equipment_benediction.common.event;

import com.pancake.equipment_benediction.EquipmentBenediction;
import com.pancake.equipment_benediction.api.IModifier;
import com.pancake.equipment_benediction.common.init.ModModifiers;
import com.pancake.equipment_benediction.common.modifier.MagneticModifier;
import com.pancake.equipment_benediction.common.modifier.ModifierHelper;
import com.pancake.equipment_benediction.common.modifier.ModifierInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber
public class PlayerEvent {
    @SubscribeEvent
    public static void onPlayerInteractRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Level level = event.getLevel();
        InteractionHand hand = event.getHand();
        Player player = event.getEntity();
        if (level.isClientSide() || hand != InteractionHand.MAIN_HAND) {
            return;
        }

        if (player.isShiftKeyDown()) {
            ItemStack mainHandItem = player.getMainHandItem();
            ModifierInstance instance = new ModifierInstance(ModModifiers.MODIFIER_REGISTRY.get().getValue(ResourceLocation.tryParse("kubejs:magnetic")), 0);
            ModifierHelper.addItemStackModifier(instance,mainHandItem);
        }
//        ModifierHelper.encodeStart(instance).ifPresent((tag) -> {
//            ModifierHelper.parse(tag).ifPresent((instance1) -> {
//                IModifier modifier = instance1.getModifier();
//                System.out.println(modifier.getRegistryName());
//            });
//        });
//
//        IForgeRegistry<IModifier> iModifiers = ModModifiers.MODIFIER_REGISTRY.get();
//        iModifiers.getValues().forEach(modifier -> System.out.println(modifier.getRegistryName()));

//        IModifier value = ModModifiers.MODIFIER_REGISTRY.get().getValue(ResourceLocation.tryParse("kubejs:stealth_speed"));

//        MagneticModifier value = ModModifiers.MAGNETIC.get();
//        if (value != null) {
//            Component displayName = value.getDisplayName();
//
//            //发送聊天信息
//            player.displayClientMessage(displayName, true);
//        }
    }

    @SubscribeEvent
    public static void onTickPlayerTick(TickEvent.PlayerTickEvent event) {
//        event.player.getMainHandItem().getOrCreateTag().getList("Modifiers", 10).forEach(x -> System.out.println("Item :" + x));
//        event.player.getPersistentData().getList("Modifiers", 10).forEach(x -> System.out.println("Player :" + x));

        Player player = event.player;
        if (player.level().isClientSide()) return;

//        ListTag modifierTags = ModifierHelper.getModifierListTag(player);
//        System.out.println("onTickPlayerTick :" + modifierTags);
    }
}