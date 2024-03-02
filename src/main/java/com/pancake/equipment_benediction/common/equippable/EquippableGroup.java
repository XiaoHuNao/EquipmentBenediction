package com.pancake.equipment_benediction.common.equippable;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.pancake.equipment_benediction.api.IEquippable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.LinkedHashMap;
import java.util.Map;

public class EquippableGroup {
    private EquippableGroup() {}

    private final LinkedHashMap<IEquippable<?>, Ingredient> EQUIPAGES = Maps.newLinkedHashMap();

    public boolean checkEquippable(Player player) {
        return EQUIPAGES
                .entrySet()
                .stream()
                .allMatch(entry -> entry.getKey().checkEquippable(player, entry.getValue()));
    }

    public EquippableGroup addGroup(IEquippable<?> equippable, Ingredient ingredient) {
        this.EQUIPAGES.put(equippable, ingredient);
        return this;
    }

    public static EquippableGroup create() {
        return new EquippableGroup();
    }

    public Map<IEquippable<?>, Ingredient> getEquipages() {
        return EQUIPAGES;
    }
}
