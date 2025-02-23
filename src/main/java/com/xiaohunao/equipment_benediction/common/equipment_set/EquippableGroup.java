package com.xiaohunao.equipment_benediction.common.equipment_set;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xiaohunao.equipment_benediction.common.equippable.IEquippable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.LinkedList;
import java.util.Map;

public class EquippableGroup {
    public final Map<IEquippable, Ingredient> equipages;
    public final LinkedList<IEquippable> blacklist;

    private EquippableGroup(Map<IEquippable, Ingredient> equipages, LinkedList<IEquippable> blacklist) {
        this.equipages = equipages;
        this.blacklist = blacklist;
    }

    public boolean isValid(LivingEntity livingEntity) {
        boolean match = equipages
                .entrySet()
                .stream()
                .allMatch(entry -> entry.getKey().checkEquippable(livingEntity, entry.getValue()));

        boolean black = blacklist
                .stream()
                .allMatch(equippable -> equippable.checkEquippable(livingEntity, Ingredient.EMPTY));

        return match && black;
    }

    public static class Builder {
        private final Map<IEquippable, Ingredient> equipages = Maps.newHashMap();
        private final LinkedList<IEquippable> blacklist = Lists.newLinkedList();

        public Builder addEquippable(IEquippable equippable, Ingredient ingredient) {
            equipages.put(equippable, ingredient);
            return this;
        }
        public Builder addEquippable(Object... equipPairs) {
            if (equipPairs == null || equipPairs.length == 0) {
                throw new IllegalArgumentException("EquipmentSet pairs cannot be null or empty");
            }
            if (equipPairs.length % 2 != 0) {
                throw new IllegalArgumentException("EquipmentSet pairs must be in pairs");
            }

            for (int i = 0; i < equipPairs.length; i += 2) {
                if (!(equipPairs[i] instanceof IEquippable equippable) || !(equipPairs[i + 1] instanceof Ingredient ingredient)) {
                    throw new IllegalArgumentException(
                            "Invalid pair type at index " + i +
                                    ". Expected IEquippable and Ingredient"
                    );
                }
                equipages.put(equippable, ingredient);
            }
            return this;
        }

        public Builder addBlacklist(IEquippable equippable) {
            this.blacklist.add(equippable);
            return this;
        }

        public EquippableGroup build() {
            return new EquippableGroup(equipages, blacklist);
        }
    }
}
