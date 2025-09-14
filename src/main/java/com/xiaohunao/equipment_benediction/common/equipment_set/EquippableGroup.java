package com.xiaohunao.equipment_benediction.common.equipment_set;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * @param exclusivityMaps true表示独占，false表示非独占
 */
public record EquippableGroup(BiMap<String, EquipmentSetBranch> equippableMaps, LinkedHashMap<EquipmentSetBranch, Boolean> exclusivityMaps) {
    public boolean isExclusive(EquipmentSetBranch data) {
        return exclusivityMaps.getOrDefault(data, true);
    }

    public static class Builder {
        private final BiMap<String, EquipmentSetBranch> equippableMaps = HashBiMap.create();
        protected final LinkedHashMap<EquipmentSetBranch, Boolean> exclusivityMaps = Maps.newLinkedHashMap();

        public Builder addEquippableSet(String name, EquipmentSetBranch equippableSetData, boolean exclusivity) {
            if (equippableSetData == null) {
                throw new IllegalArgumentException("EquippableSetData cannot be null");
            }
            equippableMaps.put(name, equippableSetData);
            this.exclusivityMaps.put(equippableSetData, exclusivity);
            return this;
        }

        public Builder addEquippableSet(String name, EquipmentSetBranch equippableSetData) {
            return addEquippableSet(name, equippableSetData, false);
        }

        @ApiStatus.Internal
        public EquippableGroup build() {
            return new EquippableGroup(equippableMaps, exclusivityMaps);
        }
    }
}
