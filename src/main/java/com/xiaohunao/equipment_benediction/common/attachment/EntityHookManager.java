package com.xiaohunao.equipment_benediction.common.attachment;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.JsonOps;
import com.xiaohunao.equipment_benediction.api.BenedictionManager;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSet;
import com.xiaohunao.equipment_benediction.common.hook.HookMap;
import com.xiaohunao.equipment_benediction.common.interfaces.IBenediction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.UnknownNullability;

public class EntityHookManager implements INBTSerializable<CompoundTag> {
    private final BiMap<IBenediction<?>, HookMap> hooks = HashBiMap.create();

    public BiMap<IBenediction<?>, HookMap> getHooks() {
        return hooks;
    }

    public EntityHookManager addHookMap(IBenediction<?> owner, HookMap hookMap) {
        hooks.put(owner, hookMap);
        return this;
    }

    public EntityHookManager removeHookMap(IBenediction<?> owner) {
        hooks.remove(owner);
        return this;
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
        compoundTag.getAllKeys().forEach(key -> {
            ResourceLocation benedictionManagerId = ResourceLocation.tryParse(key);
            if (benedictionManagerId != null) {
                IBenediction<?> benedictionByManagerId = BenedictionManager.getInstance().getBenedictionByManagerId(benedictionManagerId);
                if (benedictionByManagerId != null) {
                    Tag hookMapTag = compoundTag.get(key);
                    HookMap hookMap = HookMap.CODEC.parse(NbtOps.INSTANCE, hookMapTag).getOrThrow();
                    hooks.put(benedictionByManagerId, hookMap);
                }
            }
        });
    }

    public boolean contains(Object owner) {
        return hooks.containsKey(owner);
    }
}
