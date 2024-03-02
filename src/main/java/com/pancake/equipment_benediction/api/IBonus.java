package com.pancake.equipment_benediction.api;

import net.minecraft.world.entity.player.Player;

public interface IBonus {
    void apply(Player player);

    void clear(Player player);
}
