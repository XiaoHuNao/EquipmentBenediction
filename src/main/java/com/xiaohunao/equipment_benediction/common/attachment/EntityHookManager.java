package com.xiaohunao.equipment_benediction.common.attachment;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSet;
import com.xiaohunao.equipment_benediction.common.hook.HookMap;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.UnknownNullability;

public class EntityHookManager implements INBTSerializable<CompoundTag> {
    private final BiMap<Object, HookMap> hooks = HashBiMap.create();

    public BiMap<Object, HookMap> getHooks() {
        return hooks;
    }

    public EntityHookManager addHookMap(Object owner, HookMap hookMap) {
        hooks.put(owner, hookMap);
        return this;
    }

    public EntityHookManager removeHookMap(Object owner) {
        hooks.remove(owner);
        return this;
    }


    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        return null;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag compoundTag) {

    }

    public boolean contains(Object owner) {
        return hooks.containsKey(owner);
    }
}
