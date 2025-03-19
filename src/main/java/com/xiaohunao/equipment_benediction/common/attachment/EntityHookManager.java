package com.xiaohunao.equipment_benediction.common.attachment;

import com.google.common.collect.*;
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
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class EntityHookManager implements INBTSerializable<CompoundTag> {
    private final EquipmentSetHookManager setHookManager = new EquipmentSetHookManager(this);


    public Map<IBenediction, HookMap> getHooks() {
        Map<IBenediction, HookMap> hooks = Maps.newHashMap();

        hooks.putAll(setHookManager.getHooks());
        return hooks;
    }


    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag compoundTag = new CompoundTag();

        compoundTag.put("set_hook", setHookManager.serializeNBT(provider));
        return compoundTag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag compoundTag) {
        setHookManager.deserializeNBT(provider, compoundTag.getCompound("set_hook"));
    }

    public EntityHookManager updateActivatedHooks(LivingEntity livingEntity){
        setHookManager.updateActivatedHooks(livingEntity);
        return this;
    }

    public EquipmentSetHookManager getSetHookManager() {
        return setHookManager;
    }
}
