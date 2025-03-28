package com.xiaohunao.equipment_benediction.common.attachment;

import com.google.common.collect.Maps;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSet;
import com.xiaohunao.equipment_benediction.common.hook.HookMap;
import com.xiaohunao.equipment_benediction.common.hook.HookMapManager;
import com.xiaohunao.equipment_benediction.common.hook.IHook;
import com.xiaohunao.equipment_benediction.common.hook.special.SpecialTimeHookWrapper;
import com.xiaohunao.equipment_benediction.common.interfaces.IBenediction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.UnknownNullability;

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

    public EntityHookManager updateActivatedHooks(LivingEntity livingEntity) {
        setHookManager.updateActivatedHooks(livingEntity);
        return this;
    }

    public EquipmentSetHookManager getSetHookManager() {
        return setHookManager;
    }

    public void addSpecialTimeHook(SpecialTimeHookWrapper specialTimeHookWrapper) {
        IBenediction owner = specialTimeHookWrapper.owner();
        if (owner instanceof EquipmentSet){
            setHookManager.getSpecialTimeHookManager().addHook(specialTimeHookWrapper);
        }
    }

    public void tickSpecialTimeHook(Player player) {
        setHookManager.getSpecialTimeHookManager().tick(player);
    }

    public <T extends IHook, R> boolean containsSpecialTimeHook(IBenediction owner, T hook, HookMapManager.HookExecutor<T, R> executor) {
        if (owner instanceof EquipmentSet){
            return setHookManager.getSpecialTimeHookManager().containsSpecialTimeHook(owner, hook,executor);
        }
        return false;
    }
}
