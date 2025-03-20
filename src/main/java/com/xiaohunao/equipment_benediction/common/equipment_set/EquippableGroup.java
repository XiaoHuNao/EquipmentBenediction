package com.xiaohunao.equipment_benediction.common.equipment_set;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @param exclusivityMaps true表示独占，false表示非独占
 */
public record EquippableGroup(BiMap<String, EquippableSetData> equippableMaps, Map<EquippableSetData, Boolean> exclusivityMaps) {
    public boolean isExclusive(EquippableSetData data) {
        return exclusivityMaps.getOrDefault(data, true);
    }

    public static class Builder {
        private final BiMap<String, EquippableSetData> equippableMaps = HashBiMap.create();
        protected final Map<EquippableSetData, Boolean> exclusivityMaps = Maps.newHashMap();

        public Builder addEquippableSet(String name, EquippableSetData equippableSetData, boolean exclusivity) {
            if (equippableSetData == null) {
                throw new IllegalArgumentException("EquippableSetData cannot be null");
            }
            equippableMaps.put(name, equippableSetData);
            this.exclusivityMaps.put(equippableSetData, exclusivity);
            return this;
        }

        public Builder addEquippableSet(String name, EquippableSetData equippableSetData) {
            return addEquippableSet(name, equippableSetData, false);
        }

        public EquippableGroup build() {
            return new EquippableGroup(equippableMaps, exclusivityMaps);
        }
    }
}
