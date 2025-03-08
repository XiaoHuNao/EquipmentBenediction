package com.xiaohunao.equipment_benediction.common.attachment;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.xiaohunao.equipment_benediction.api.manager.BenedictionManager;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSet;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquippableSetData;
import com.xiaohunao.equipment_benediction.common.hook.HookMap;
import com.xiaohunao.equipment_benediction.common.interfaces.IBenediction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Collection;
import java.util.Collections;

public class EntityHookManager implements INBTSerializable<CompoundTag> {
    private final BiMap<IBenediction<?>, HookMap> hooks = HashBiMap.create();
    private final BiMap<EquipmentSet, HookMap> equippedHooks = HashBiMap.create();
    private final Multimap<EquipmentSet, EquippableSetData> equipmentSetHookMap = HashMultimap.create();

    public BiMap<IBenediction<?>, HookMap> getHooks() {
        return hooks;
    }

    public EntityHookManager removeHookMap(IBenediction<?> owner) {
        hooks.remove(owner);
        return this;
    }

    public HookMap getHookMap(IBenediction<?> owner) {
        return hooks.get(owner);
    }

    public boolean isEmpty() {
        return hooks.isEmpty();
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag compoundTag = new CompoundTag();

        CompoundTag hooksTag = new CompoundTag();
        hooks.forEach((owner, hookMap) -> {
            Tag hookMapTag = HookMap.CODEC.encodeStart(NbtOps.INSTANCE, hookMap).getOrThrow();
            ResourceLocation benedictionManagerId = BenedictionManager.getInstance().getBenedictionManagerId(owner);
            hooksTag.put(benedictionManagerId.toString(), hookMapTag);
        });
        compoundTag.put("hooks", hooksTag);


        CompoundTag equipmentSetDataTag = new CompoundTag();
        equipmentSetHookMap.asMap().forEach((equipmentSet, setDataCollection) -> {
            ResourceLocation setId = BenedictionManager.getInstance().getBenedictionManagerId(equipmentSet);

            ListTag setDataListTag = new ListTag();
            
            for (EquippableSetData setData : setDataCollection) {
                CompoundTag setDataTag = new CompoundTag();
                Tag setDataHookMapTag = HookMap.CODEC.encodeStart(NbtOps.INSTANCE, setData.getHookMap()).getOrThrow();
                setDataTag.put("hook_map", setDataHookMapTag);
                setDataListTag.add(setDataTag);
            }
            
            equipmentSetDataTag.put(setId.toString(), setDataListTag);
        });
        compoundTag.put("equipment_set_data", equipmentSetDataTag);
        
        return compoundTag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag compoundTag) {
        hooks.clear();
        equippedHooks.clear();
        equipmentSetHookMap.clear();

        if (compoundTag.contains("hooks")) {
            CompoundTag hooksTag = compoundTag.getCompound("hooks");
            hooksTag.getAllKeys().forEach(key -> {
                ResourceLocation benedictionManagerId = ResourceLocation.tryParse(key);
                if (benedictionManagerId != null) {
                    IBenediction<?> benediction = BenedictionManager.getInstance().getBenedictionByManagerId(benedictionManagerId);
                    if (benediction != null) {
                        Tag hookMapTag = hooksTag.get(key);
                        HookMap hookMap = HookMap.CODEC.parse(NbtOps.INSTANCE, hookMapTag).getOrThrow();
                        hooks.put(benediction, hookMap);

                        if (benediction instanceof EquipmentSet equipmentSet){
                            equippedHooks.put(equipmentSet, hookMap);
                        }
                    }
                }
            });
        }

        if (compoundTag.contains("equipment_set_data")) {
            CompoundTag equipmentSetDataTag = compoundTag.getCompound("equipment_set_data");
            equipmentSetDataTag.getAllKeys().forEach(key -> {
                ResourceLocation setId = ResourceLocation.tryParse(key);
                if (setId != null) {
                    IBenediction<?> benediction = BenedictionManager.getInstance().getBenedictionByManagerId(setId);
                    ListTag list = equipmentSetDataTag.getList(key, Tag.TAG_COMPOUND);

                    list.forEach(setData -> {
                        if (setData instanceof CompoundTag setDataTag && benediction instanceof EquipmentSet equipmentSet) {
                            if (setDataTag.contains("hook_map")) {
                                Tag setDataHookMapTag = setDataTag.get("hook_map");
                                HookMap setDataHookMap = HookMap.CODEC.parse(NbtOps.INSTANCE, setDataHookMapTag).getOrThrow();

                                for (EquippableSetData equalsSetData : equipmentSet.getEquippableGroup().getEquippableSets()) {
                                    if (equalsSetData.getHookMap().contentEquals(setDataHookMap)) {
                                        equipmentSetHookMap.put(equipmentSet, equalsSetData);
                                        break;
                                    }
                                }
                            }
                        }
                    });
                }
            });
        }
    }

    public boolean contains(Object owner) {
        return hooks.containsKey(owner);
    }

    public void updateEquippableSetData(EquipmentSet equipmentSet, EquippableSetData equippableSetData, boolean takeEffect) {
        boolean containsKey = equipmentSetHookMap.containsKey(equipmentSet);
        if (takeEffect){
            if (!containsKey){
                equipmentSetHookMap.put(equipmentSet, equippableSetData);
                equippedHooks.put(equipmentSet, equippableSetData.getHookMap());
                hooks.put(equipmentSet, equippableSetData.getHookMap());
            }else {
                equipmentSetHookMap.put(equipmentSet, equippableSetData);
                HookMap hookMap = hooks.get(equipmentSet);
                if (hookMap != null){
                    HookMap merge = hookMap.merge(equippableSetData.getHookMap());
                    hooks.put(equipmentSet, merge);
                    equippedHooks.put(equipmentSet, merge);
                }
            }
        }else {
            if (containsKey) {
                equipmentSetHookMap.remove(equipmentSet, equippableSetData);
                HookMap hookMap = hooks.get(equipmentSet);
                if (hookMap != null) {
                    HookMap deduplicate = hookMap.deduplicate(equippableSetData.getHookMap());
                    if (deduplicate.isEmpty()) {
                        hooks.remove(equipmentSet);
                        equippedHooks.remove(equipmentSet);
                    } else {
                        hooks.put(equipmentSet, deduplicate);
                        equippedHooks.put(equipmentSet, deduplicate);
                    }
                }
            }
        }
    }


    public BiMap<EquipmentSet, HookMap> getEquipmentSetHookMap() {
        return equippedHooks;
    }
    public Multimap<EquipmentSet, EquippableSetData> getEquipmentSetDataHookMap() {
        return equipmentSetHookMap;
    }
}
