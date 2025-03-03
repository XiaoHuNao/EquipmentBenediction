package com.xiaohunao.equipment_benediction.common.equipment_set;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import com.xiaohunao.equipment_benediction.common.hook.HookMap;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class EquippableGroup {
    protected final Map<EquippableSetData, Integer> equippableSets;

    public EquippableGroup(Map<EquippableSetData, Integer> equippableSets) {
        this.equippableSets = equippableSets;
    }

    public Map<EquippableSetData, Integer> getEquippableSets() {
        return equippableSets;
    }

    public static class Builder {
        private final Map<EquippableSetData, Integer> equippableSets = Maps.newLinkedHashMap();

        public Builder addEquippableSet(EquippableSetData equippableSetData){
            return addEquippableSet(1, equippableSetData);
        }

        public Builder addEquippableSet(int priority, EquippableSetData equippableSetData) {
            if (equippableSetData == null) {
                throw new IllegalArgumentException("EquippableSetData cannot be null");
            }
            equippableSets.put(equippableSetData, priority);
            LinkedHashMap<EquippableSetData, Integer> collect = equippableSets.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue())
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (e1, e2) -> e1,
                            LinkedHashMap::new
                    ));
            equippableSets.clear();
            equippableSets.putAll(collect);
            return this;
        }

        public EquippableGroup build() {
            return new EquippableGroup(equippableSets);
        }
        
    }


}
