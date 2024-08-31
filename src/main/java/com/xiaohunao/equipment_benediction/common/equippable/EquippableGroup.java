package com.xiaohunao.equipment_benediction.common.equippable;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.xiaohunao.equipment_benediction.api.IEquippable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.*;

public class EquippableGroup {
    private EquippableGroup() {}

    public BiMap<IEquippable, Ingredient> equippables = HashBiMap.create();
    public Set<IEquippable> matchEquippable = Sets.newHashSet();
    public LinkedList<IEquippable> blacklist = Lists.newLinkedList();

    public EquippableGroup addGroup(IEquippable equippable, Ingredient ingredient) {
        this.equippables.put(equippable, ingredient);
        return this;
    }
    public EquippableGroup addBlacklist(IEquippable equippable) {
        this.blacklist.add(equippable);
        return this;
    }

    public boolean checkEquippable(LivingEntity livingEntity) {
        boolean match = equippables
                .entrySet()
                .stream()
                .allMatch(entry -> {
                    boolean checked = entry.getKey().checkEquippable(livingEntity, entry.getValue());
                    if (checked) {
                        matchEquippable.add(entry.getKey());
                    }
                    return checked;
                });

        boolean black = blacklist
                .stream()
                .allMatch(equippable -> equippable.checkEquippable(livingEntity, Ingredient.EMPTY));

        return match && black;
    }

    public static EquippableGroup create() {
        return new EquippableGroup();
    }

    public boolean checkBlacklist(IEquippable equippable) {
        return blacklist.stream().anyMatch(equippable1 -> equippable1.equals(equippable));
    }


}
