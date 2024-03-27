package com.pancake.equipment_benediction.common.equippable;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.pancake.equipment_benediction.api.IEquippable;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.LinkedHashMap;
import java.util.Map;

public class EquippableGroup {
    private EquippableGroup() {}

    private final LinkedHashMap<IEquippable<?>, Ingredient> EQUIPAGES = Maps.newLinkedHashMap();

    public boolean checkEquippable(LivingEntity livingEntity) {
        return EQUIPAGES
                .entrySet()
                .stream()
                .allMatch(entry -> entry.getKey().checkEquippable(livingEntity, entry.getValue()));
    }

    public EquippableGroup addGroup(IEquippable<?> equippable, Ingredient ingredient) {
        this.EQUIPAGES.put(equippable, ingredient);
        return this;
    }
    public EquippableGroup addGroup(IEquippable<?> equippable, Item item) {
        return addGroup(equippable, Ingredient.of(item));
    }
    public EquippableGroup addGroup(IEquippable<?> equippable, Item... items) {
        return addGroup(equippable, Ingredient.of(items));
    }
    public EquippableGroup addGroup(IEquippable<?> equippable, TagKey<Item> tagKey) {
        return addGroup(equippable, Ingredient.of(tagKey));
    }

    public static EquippableGroup create() {
        return new EquippableGroup();
    }

    public Map<IEquippable<?>, Ingredient> getEquipages() {
        return EQUIPAGES;
    }
}
