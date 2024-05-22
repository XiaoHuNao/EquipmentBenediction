package com.pancake.equipment_benediction.common.capability;

import com.pancake.equipment_benediction.common.event.PlayerContainerChangeEvent;
import com.pancake.equipment_benediction.common.init.ModCapability;
import com.pancake.equipment_benediction.common.modifier.ModifierHelper;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;


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
        Inventory inventory = player.getInventory();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack newStack = inventory.getItem(i);
            ItemStack oldStack = this.inventory.getItem(i);
            if (!ItemStack.matches(oldStack, newStack)) {
                this.inventory.setItem(i, newStack.copy());
                PlayerContainerChangeEvent equipmentChangeEvent = new PlayerContainerChangeEvent(player, EquipmentSlot.MAINHAND, oldStack, newStack);
                MinecraftForge.EVENT_BUS.post(equipmentChangeEvent);
            }
        }
        ItemStack selected = inventory.getSelected();
        if (!ItemStack.matches(this.selected, selected)) {
            PlayerContainerChangeEvent equipmentChangeEvent = new PlayerContainerChangeEvent(player, EquipmentSlot.MAINHAND, this.selected, selected);
            MinecraftForge.EVENT_BUS.post(equipmentChangeEvent);
            this.selected = selected.copy();
        }
    }

    public void updateInventory() {
        Inventory inventory = player.getInventory();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack itemStack = inventory.getItem(i);
            ModifierHelper.removeNonViableModifiers(itemStack);
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
