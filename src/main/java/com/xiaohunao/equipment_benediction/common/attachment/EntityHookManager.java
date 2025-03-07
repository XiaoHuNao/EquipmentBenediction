package com.xiaohunao.equipment_benediction.common.attachment;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.xiaohunao.equipment_benediction.api.manager.BenedictionManager;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSet;
import com.xiaohunao.equipment_benediction.common.hook.HookMap;
import com.xiaohunao.equipment_benediction.common.interfaces.IBenediction;
import com.xiaohunao.equipment_benediction.common.modifier.Modifier;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.UnknownNullability;

public class EntityHookManager implements INBTSerializable<CompoundTag> {
    private final BiMap<IBenediction<?>, HookMap> hooks = HashBiMap.create();
    private final BiMap<EquipmentSet, HookMap> equipmentSetHookMap = HashBiMap.create();
    private final BiMap<Modifier, HookMap> modifierHookMap = HashBiMap.create();

    public BiMap<IBenediction<?>, HookMap> getHooks() {
        return hooks;
    }

    public EntityHookManager addHookMap(IBenediction<?> owner, HookMap hookMap) {
        hooks.put(owner, hookMap);
        if (owner instanceof EquipmentSet equipmentSet){
            equipmentSetHookMap.put(equipmentSet, hookMap);
        }

        if (owner instanceof Modifier modifier){
            modifierHookMap.put(modifier, hookMap);
        }
        return this;
    }

    public EntityHookManager removeHookMap(IBenediction<?> owner) {
        hooks.remove(owner);
        return this;
    }

    public HookMap getHookMap(IBenediction<?> owner) {
        return hooks.get(owner);
    }

    public BiMap<EquipmentSet, HookMap> getEquipmentSetHookMap() {
        return equipmentSetHookMap;
    }

    public BiMap<Modifier, HookMap> getModifierHookMap() {
        return modifierHookMap;
    }

    public HookMap getEquipmentSetHookMap(EquipmentSet equipmentSet) {
        return equipmentSetHookMap.get(equipmentSet);
    }

    public HookMap getModifierHookMap(Modifier modifier) {
        return modifierHookMap.get(modifier);
    }

    public boolean isEmpty() {
        return hooks.isEmpty();
    }

    public boolean isModifierEmpty() {
        return modifierHookMap.isEmpty();
    }

    public boolean isEquipmentSetEmpty() {
        return equipmentSetHookMap.isEmpty();
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag compoundTag = new CompoundTag();
        hooks.forEach((owner, hookMap) -> {
            Tag hookMapTag = HookMap.CODEC.encodeStart(NbtOps.INSTANCE, hookMap).getOrThrow();
            ResourceLocation benedictionManagerId = BenedictionManager.getInstance().getBenedictionManagerId(owner);
            compoundTag.put(benedictionManagerId.toString(), hookMapTag);
        });
        return compoundTag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag compoundTag) {
        hooks.clear();
        equipmentSetHookMap.clear();
        modifierHookMap.clear();
        compoundTag.getAllKeys().forEach(key -> {
            ResourceLocation benedictionManagerId = ResourceLocation.tryParse(key);
            if (benedictionManagerId != null) {
                IBenediction<?> benediction = BenedictionManager.getInstance().getBenedictionByManagerId(benedictionManagerId);
                if (benediction != null) {
                    Tag hookMapTag = compoundTag.get(key);
                    HookMap hookMap = HookMap.CODEC.parse(NbtOps.INSTANCE, hookMapTag).getOrThrow();
                    if (benediction instanceof EquipmentSet equipmentSet){
                        equipmentSetHookMap.put(equipmentSet, hookMap);
                    }
                    if (benediction instanceof Modifier modifier){
                        modifierHookMap.put(modifier, hookMap);
                    }
                    hooks.put(benediction, hookMap);
                }
            }
        });
    }

    public boolean contains(Object owner) {
        return hooks.containsKey(owner);
    }
}
