package com.pancake.equipment_benediction.common.event;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PlayerEquipmentChangeEvent extends net.minecraftforge.event.entity.player.PlayerEvent {
    private final EquipmentSlot slot;
    private final ItemStack from;
    private final ItemStack to;

    public PlayerEquipmentChangeEvent(Player player, EquipmentSlot slot, @NotNull ItemStack from, @NotNull ItemStack to) {
        super(player);
        this.slot = slot;
        this.from = from;
        this.to = to;
    }

    public EquipmentSlot getSlot() {
        return this.slot;
    }

    public @NotNull ItemStack getFrom() {
        return this.from;
    }

    public @NotNull ItemStack getTo() {
        return this.to;
    }
}
