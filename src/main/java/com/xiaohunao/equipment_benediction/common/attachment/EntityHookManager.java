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
    private final EquipmentSetHookManager setHookManager = new EquipmentSetHookManager(this);

    public BiMap<IBenediction<?>, HookMap> getHooks() {
        return hooks;
    }

    public EntityHookManager removeHookMap(IBenediction<?> owner) {
        hooks.remove(owner);
        return this;
    }

    public EntityHookManager addHookMap(IBenediction<?> owner, HookMap hookMap) {
        hooks.put(owner, hookMap);
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

        compoundTag.put("equipped_hooks", setHookManager.serializeNBT(provider));
        return compoundTag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag compoundTag) {
        hooks.clear();
        setHookManager.deserializeNBT(provider, compoundTag);
    }

    public boolean contains(IBenediction<?> owner) {
        return hooks.containsKey(owner);
    }

    public EquipmentSetHookManager getSetHookManager() {
        return setHookManager;
    }
}
