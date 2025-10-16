package com.xiaohunao.equipment_benediction.common.attachment;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.xiaohunao.equipment_benediction.api.manager.EquipmentSetManager;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSet;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSetBranch;
import com.xiaohunao.equipment_benediction.common.hook.HookMap;
import com.xiaohunao.equipment_benediction.common.hook.HookMapManager;
import com.xiaohunao.equipment_benediction.common.hook.special.SpecialTimeHook;
import com.xiaohunao.equipment_benediction.common.hook.special.SpecialTimeHookManager;
import com.xiaohunao.equipment_benediction.common.hook.special.SpecialTimeHookWrapper;
import com.xiaohunao.equipment_benediction.common.init.EBAttachments;
import com.xiaohunao.equipment_benediction.common.init.EBHookTypes;
import com.xiaohunao.equipment_benediction.common.interfaces.IBenediction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EquipmentSetHookManager implements INBTSerializable<CompoundTag> {
    private static final Logger LOGGER = LoggerFactory.getLogger(EquipmentSetHookManager.class);
    private final SpecialTimeHookManager specialTimeHookManager;
    private final EntityHookManager entityHookManager;
    private final EquipmentSetManager equipmentSetManager = EquipmentSetManager.getInstance();

    //激活的
    private final Multimap<EquipmentSet, EquipmentSetBranch> activatedSetBranch = HashMultimap.create();
    //选中的
    private final Multimap<EquipmentSet, EquipmentSetBranch>  selectedEquipped = HashMultimap.create();

    public EquipmentSetHookManager(EntityHookManager entityHookManager) {
        this.entityHookManager = entityHookManager;
        this.specialTimeHookManager = new SpecialTimeHookManager(entityHookManager);
    }


    public Multimap<EquipmentSet, EquipmentSetBranch> getActivatedSetBranch() {
        return activatedSetBranch;
    }

    public Multimap<EquipmentSet, EquipmentSetBranch> getSelectedEquipped() {
        return selectedEquipped;
    }

    @Override
    @UnknownNullability
    public CompoundTag serializeNBT(@NotNull HolderLookup.Provider provider) {
        CompoundTag compoundTag = new CompoundTag();

        compoundTag.put("activated_equipped", serializeEquippedData(activatedSetBranch));
        compoundTag.put("selected_equipped", serializeEquippedData(selectedEquipped));
        return compoundTag;
    }

    @Override
    public void deserializeNBT(@NotNull HolderLookup.Provider provider, @NotNull CompoundTag compoundTag) {
        activatedSetBranch.clear();
        selectedEquipped.clear();

        deserializeEquippedData(compoundTag.getCompound("activated_equipped"), activatedSetBranch);
        deserializeEquippedData(compoundTag.getCompound("selected_equipped"),  selectedEquipped);
    }

    private CompoundTag serializeEquippedData(Multimap<EquipmentSet, EquipmentSetBranch> data) {
        CompoundTag tag = new CompoundTag();
        data.asMap().forEach((equipmentSet, equippableSetData) -> {
            ResourceLocation setId = equipmentSetManager.getResource(equipmentSet);
            ListTag setDataListTag = new ListTag();
            for (EquipmentSetBranch setData : equippableSetData) {
                ResourceLocation branchResource = equipmentSetManager.getBranchResource(setData);
                if (branchResource == null) {
                    LOGGER.warn("Unknown set data: '{}', skipped.", setData);
                } else {
                    setDataListTag.add(StringTag.valueOf(branchResource.toString()));
                }
            }
            tag.put(setId.toString(), setDataListTag);
        });
        return tag;
    }

    private void deserializeEquippedData(CompoundTag tag, Multimap<EquipmentSet, EquipmentSetBranch> target) {
            tag.getAllKeys().forEach(key -> {
            ResourceLocation setId = ResourceLocation.tryParse(key);
            if (setId != null) {
                IBenediction benediction = equipmentSetManager.getResource(setId);
                if (benediction instanceof EquipmentSet equipmentSet) {
                    ListTag listTag = tag.getList(key, Tag.TAG_STRING);
                    listTag.forEach(element -> {
                        if (element instanceof StringTag name) {
                            target.put(equipmentSet, equipmentSetManager.getBranchResource(ResourceLocation.tryParse(name.getAsString())));
                        }
                    });
                }
            }
        });
    }
    public EquipmentSetHookManager updateSelectedEquipped(EquipmentSet equipmentSet, EquipmentSetBranch equippableSetData, boolean clicked){
        if (clicked) {
            selectedEquipped.put(equipmentSet, equippableSetData);
        } else {
            selectedEquipped.remove(equipmentSet, equippableSetData);
        }
        return this;
    }

    public EquipmentSetHookManager updateEquippable(LivingEntity livingEntity, EquipmentSet equipmentSet, EquipmentSetBranch setBranch, boolean takeEffect){
        boolean containsKey = activatedSetBranch.containsKey(equipmentSet);

        if (takeEffect) {
            handleEquipEquipment(livingEntity, equipmentSet, setBranch);
        } else {
            handleUnequipEquipment(livingEntity, equipmentSet, setBranch, containsKey);
        }
        return this;
    }

    private void handleEquipEquipment(LivingEntity livingEntity, EquipmentSet equipmentSet, EquipmentSetBranch setBranch) {
        activatedSetBranch.put(equipmentSet, setBranch);
        HookMapManager.postHooks(EBHookTypes.ACTIVATED_SET_BRANCH.get(),(owner, hook,original) -> {
            hook.onActivatedSetBranch(livingEntity,equipmentSet,setBranch);
            return null;
        }, livingEntity);
    }

    private void handleUnequipEquipment(LivingEntity livingEntity, EquipmentSet equipmentSet, EquipmentSetBranch setBranch, boolean containsKey) {
        if (!containsKey) {
            return;
        }
        activatedSetBranch.remove(equipmentSet, setBranch);

        List<SpecialTimeHookWrapper> wrappersToRemove = new ArrayList<>();
        specialTimeHookManager.getSpecialTimeHooks().keySet().forEach(wrapper -> {
            setBranch.hookMap().hooks().values().forEach(hook -> {
                if (hook instanceof SpecialTimeHook specialTimeHook) {
                    if (wrapper.owner() == equipmentSet && wrapper.specialTimeHook().getHook().equals(specialTimeHook.getHook())) {
                        wrappersToRemove.add(wrapper);
                    }
                }
            });
        });

        wrappersToRemove.forEach(wrapper -> {
            specialTimeHookManager.getSpecialTimeHooks().remove(wrapper);
        });

        HookMapManager.postHooks(EBHookTypes.SUPPRESSION_SET_BRANCH.get(),(owner, hook,original) -> {
            hook.onSuppressionSetBranch(livingEntity,equipmentSet,setBranch);
            return null;
        }, livingEntity);
    }


    public void sync(LivingEntity livingEntity) {
        livingEntity.setData(EBAttachments.ENTITY_HOOK_MANAGER, entityHookManager);
    }

    public Map<IBenediction, HookMap> getHooks() {
        Map<IBenediction, HookMap> result = new HashMap<>();
        activatedSetBranch.asMap().forEach((equipmentSet, datas) -> {
            if (datas.isEmpty()) {
                return;
            }
            HookMap hookMap = null;
            for (EquipmentSetBranch data : datas) {
                if (hookMap == null){
                    hookMap = data.hookMap();
                }else {
                    hookMap = hookMap.merge(data.hookMap());
                }
            }
            result.put(equipmentSet, hookMap);
        });
        return result;
    }

    public EquipmentSetHookManager updateActivatedHooks(LivingEntity livingEntity) {
        List<Map.Entry<EquipmentSet, EquipmentSetBranch>> toRemove = new ArrayList<>();
        activatedSetBranch.entries().forEach(entry -> {
            if (!entry.getValue().isValid(livingEntity)) {
                toRemove.add(entry);
            }
        });

        toRemove.forEach(entry -> {
            activatedSetBranch.remove(entry.getKey(), entry.getValue());
        });
        
        return this;
    }

    public SpecialTimeHookManager getSpecialTimeHookManager() {
        return specialTimeHookManager;
    }
}
