package com.xiaohunao.equipment_benediction.common.equipment_set;


import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.xiaohunao.equipment_benediction.api.manager.BenedictionManager;
import com.xiaohunao.equipment_benediction.api.manager.EquipmentSetManager;
import com.xiaohunao.equipment_benediction.common.attachment.EquipmentSetHookManager;
import com.xiaohunao.equipment_benediction.common.hook.HookMap;
import com.xiaohunao.equipment_benediction.common.hook.HookMapManager;
import com.xiaohunao.equipment_benediction.common.interfaces.IBenediction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import java.util.Map;


public class EquipmentSet implements IBenediction {
    private final static EquipmentSetManager manager = EquipmentSetManager.getInstance();

    protected HookMap hookMap;
    protected EquippableGroup equippableGroup;

    protected EquipmentSet() {
        HookMap.Builder hookBuilder = HookMap.builder();
        EquippableGroup.Builder equippableGroupBuilder = new EquippableGroup.Builder();

        init(hookBuilder, equippableGroupBuilder);

        this.hookMap = hookBuilder.build();
        this.equippableGroup = equippableGroupBuilder.build();

        HookMapManager.getInstance().register(this, hookMap);
    }

    protected void init(HookMap.Builder hook, EquippableGroup.Builder equippableGroup) {}

    public EquippableGroup getEquippableGroup() {
        return equippableGroup;
    }


    public ResourceLocation getBranchLocation(String branchName) {
        if (getEquippableGroup().getEquippableMaps().containsKey(branchName)){
            ResourceLocation resource = manager.getResource(this);
            return ResourceLocation.fromNamespaceAndPath(resource.getNamespace(), resource.getPath() + "/" + branchName);
        }
        return null;
    }
}
