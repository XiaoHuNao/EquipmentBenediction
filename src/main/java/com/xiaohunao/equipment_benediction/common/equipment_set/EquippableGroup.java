package com.xiaohunao.equipment_benediction.common.equipment_set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;

public class EquippableGroup {
    private final Set<EquippableSetData> equippableSets;
    private final Map<EquippableSetData, Boolean> exclusivityMap; // true表示独占，false表示非独占

    public EquippableGroup(Set<EquippableSetData> equippableSets, Map<EquippableSetData, Boolean> exclusivityMap) {
        this.equippableSets = equippableSets;
        this.exclusivityMap = exclusivityMap;
    }

    public Set<EquippableSetData> getEquippableSets() {
        return equippableSets;
    }

    public Map<EquippableSetData, Boolean> getExclusivityMap() {
        return exclusivityMap;
    }

    // 设置某个效果是否独占
    public void setExclusive(EquippableSetData data, boolean exclusive) {
        if (equippableSets.contains(data)) {
            exclusivityMap.put(data, exclusive);
        }
    }

    // 检查某个效果是否独占
    public boolean isExclusive(EquippableSetData data) {
        return exclusivityMap.getOrDefault(data, true);
    }

    public static class Builder {
        private final LinkedHashSet<EquippableSetData> equippableSets = Sets.newLinkedHashSet();
        protected final Map<EquippableSetData, Boolean> exclusivityMap = Maps.newHashMap();

        public Builder addEquippableSet(EquippableSetData equippableSetData, boolean exclusivity) {
            if (equippableSetData == null) {
                throw new IllegalArgumentException("EquippableSetData cannot be null");
            }
            equippableSets.add(equippableSetData);
            this.exclusivityMap.put(equippableSetData, exclusivity);
            return this;
        }

        public Builder addEquippableSet(EquippableSetData equippableSetData) {
            return addEquippableSet(equippableSetData, false);
        }

        public EquippableGroup build() {
            return new EquippableGroup(equippableSets, exclusivityMap);
        }
    }
}
