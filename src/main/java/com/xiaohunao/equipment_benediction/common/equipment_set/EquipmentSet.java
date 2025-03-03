package com.xiaohunao.equipment_benediction.common.equipment_set;


import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.xiaohunao.equipment_benediction.common.hook.HookMap;
import com.xiaohunao.equipment_benediction.common.hook.HookMapManager;
import com.xiaohunao.equipment_benediction.common.interfaces.IBenediction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;

import java.util.Map;


public class EquipmentSet  implements IBenediction<CompoundTag> {
    private final HookMap hookMap;
    protected final EquippableGroup equippableGroup;
    protected final BiMap<EquippableSetData,HookMap> setDataHookMap = HashBiMap.create();

    public EquipmentSet() {
        HookMap.Builder hookBuilder = HookMap.builder();
        EquippableGroup.Builder equippableGroupBuilder = new EquippableGroup.Builder();

        init(hookBuilder,equippableGroupBuilder);

        this.hookMap = hookBuilder.build();
        this.equippableGroup = equippableGroupBuilder.build();

        HookMapManager.getInstance().register(this,hookMap);

        equippableGroup.equippableSets.keySet().forEach(setData -> {
            HookMap dataHookMap = setData.getHookMap();
            HookMapManager.getInstance().register(this, dataHookMap);
            setDataHookMap.put(setData, dataHookMap);
        });
    }
    protected EquipmentSet(HookMap hooks, EquippableGroup equippableGroup) {
        this.hookMap = hooks;
        this.equippableGroup = equippableGroup;
    }

    protected void init(HookMap.Builder hook, EquippableGroup.Builder equippableGroup) {}

    public EquippableGroup getEquippableGroup() {
        return equippableGroup;
    }

    public BiMap<EquippableSetData, HookMap> getSetDataHookMap() {
        return setDataHookMap;
    }

    public EquippableSetData getEquippableSet(LivingEntity livingEntity) {
        EquippableSetData bestSetData = null;
        int maxValue = 0;

        for (Map.Entry<EquippableSetData, Integer> entry : equippableGroup.equippableSets.entrySet()) {
            if (entry.getKey().isValid(livingEntity)) {
                if (entry.getValue() > maxValue) {
                    maxValue = entry.getValue();
                    bestSetData = entry.getKey();
                }
            }
        }
        return bestSetData;
    }

    public HookMap getHookMap(LivingEntity livingEntity) {
        return this.hookMap.merge(getEquippableSet(livingEntity).getHookMap());
    }

    @Override
    public CompoundTag serializeNBT() {
        return null;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {

    }
}
