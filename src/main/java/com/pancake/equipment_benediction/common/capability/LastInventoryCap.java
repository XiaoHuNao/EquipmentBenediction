package com.pancake.equipment_benediction.common.capability;

import com.pancake.equipment_benediction.common.event.PlayerEquipmentChangeEvent;
import com.pancake.equipment_benediction.common.init.ModCapability;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Map;


public class LastInventoryCap {
    private final Player player;
    private final Inventory inventory;
    private ItemStack selected = ItemStack.EMPTY;

    public LastInventoryCap(Player player) {
        this.player = player;
        this.inventory = new Inventory(player);
    }


    public static LazyOptional<LastInventoryCap> get(Player player){
        return player.getCapability(ModCapability.LAST_INVENTORY);
    }

    public void update() {
//        for (EquipmentSlot slot : EquipmentSlot.values()) {
//            ItemStack newStack = player.getItemBySlot(slot);
//            ItemStack oldStack = lastItems.get(slot);
//
//            if(oldStack == null) {
//                oldStack = ItemStack.EMPTY;
//            }
//            if (newStack.getItem() == Items.AIR) {
//                newStack = ItemStack.EMPTY;
//            }
//
//            if (!ItemStack.matches(oldStack, newStack)) {
//                lastItems.put(slot, newStack.copy());
//                PlayerEquipmentChangeEvent equipmentChangeEvent = new PlayerEquipmentChangeEvent(player, slot, oldStack, newStack);
//                MinecraftForge.EVENT_BUS.post(equipmentChangeEvent);
//            }
//        }
        Inventory inventory = player.getInventory();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack newStack = inventory.getItem(i);
            ItemStack oldStack = this.inventory.getItem(i);
            if (!ItemStack.matches(oldStack, newStack)) {
                this.inventory.setItem(i, newStack.copy());
                PlayerEquipmentChangeEvent equipmentChangeEvent = new PlayerEquipmentChangeEvent(player, EquipmentSlot.MAINHAND, oldStack, newStack);
                MinecraftForge.EVENT_BUS.post(equipmentChangeEvent);
            }
        }
        ItemStack selected1 = inventory.getSelected();
        if (!ItemStack.matches(selected, selected1)) {
            PlayerEquipmentChangeEvent equipmentChangeEvent = new PlayerEquipmentChangeEvent(player, EquipmentSlot.MAINHAND, selected, selected1);
            MinecraftForge.EVENT_BUS.post(equipmentChangeEvent);
            selected = selected1.copy();
        }
    }


    public static class Provider implements ICapabilityProvider {
        private final LazyOptional<LastInventoryCap> instance;

        public Provider(Player player) {
            this.instance = LazyOptional.of(() -> new LastInventoryCap(player));
        }

        @Override
        public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            return ModCapability.LAST_INVENTORY.orEmpty(cap, instance);
        }
    }
}
