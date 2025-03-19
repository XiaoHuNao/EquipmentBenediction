package com.xiaohunao.equipment_benediction.common.attachment;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.xiaohunao.equipment_benediction.api.manager.EquipmentSetManager;
import com.xiaohunao.equipment_benediction.common.context.LivingEquipmentChangeContext;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSet;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquippableSetData;
import com.xiaohunao.equipment_benediction.common.hook.HookMap;
import com.xiaohunao.equipment_benediction.common.hook.HookMapManager;
import com.xiaohunao.equipment_benediction.common.init.EBAttachments;
import com.xiaohunao.equipment_benediction.common.init.EBHookTypes;
import com.xiaohunao.equipment_benediction.common.interfaces.IBenediction;
import com.xiaohunao.equipment_benediction.common.network.EntityHookManagerSyncPayload;
import com.xiaohunao.equipment_benediction.common.network.PostEquipOrUnequipEquipmentHookPayload;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.network.PacketDistributor;
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
    private final EntityHookManager entityHookManager;
    private final EquipmentSetManager equipmentSetManager = EquipmentSetManager.getInstance();

    //激活的
    private final Multimap<EquipmentSet, EquippableSetData>  activatedEquipped = HashMultimap.create();
    //选中的
    private final Multimap<EquipmentSet, EquippableSetData>  selectedEquipped = HashMultimap.create();

    public EquipmentSetHookManager(EntityHookManager entityHookManager) {
        this.entityHookManager = entityHookManager;
    }


    public Multimap<EquipmentSet, EquippableSetData> getActivatedEquipped() {
        return activatedEquipped;
    }

    public Multimap<EquipmentSet, EquippableSetData> getSelectedEquipped() {
        return selectedEquipped;
    }

    @Override
    @UnknownNullability
    public CompoundTag serializeNBT(@NotNull HolderLookup.Provider provider) {
        CompoundTag compoundTag = new CompoundTag();

        compoundTag.put("activated_equipped", serializeEquippedData(activatedEquipped));
        compoundTag.put("selected_equipped", serializeEquippedData(selectedEquipped));
        return compoundTag;
    }

    @Override
    public void deserializeNBT(@NotNull HolderLookup.Provider provider, @NotNull CompoundTag compoundTag) {
        activatedEquipped.clear();
        selectedEquipped.clear();

        deserializeEquippedData(compoundTag.getCompound("activated_equipped"), activatedEquipped);
        deserializeEquippedData(compoundTag.getCompound("selected_equipped"),  selectedEquipped);
    }

    private CompoundTag serializeEquippedData(Multimap<EquipmentSet, EquippableSetData> data) {
        CompoundTag tag = new CompoundTag();
        data.asMap().forEach((equipmentSet, equippableSetData) -> {
            ResourceLocation setId = equipmentSetManager.getResource(equipmentSet);
            ListTag setDataListTag = new ListTag();
            equippableSetData.forEach(setData -> {
                ResourceLocation branchResource = equipmentSetManager.getBranchResource(setData);
                setDataListTag.add(StringTag.valueOf(branchResource.toString()));
            });
            tag.put(setId.toString(), setDataListTag);
        });
        return tag;
    }

    private void deserializeEquippedData(CompoundTag tag, Multimap<EquipmentSet, EquippableSetData> target) {
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
    public EquipmentSetHookManager updateSelectedEquipped(EquipmentSet equipmentSet, EquippableSetData equippableSetData, boolean clicked){
        if (clicked) {
            selectedEquipped.put(equipmentSet, equippableSetData);
        } else {
            selectedEquipped.remove(equipmentSet, equippableSetData);
        }
        return this;
    }

    public EquipmentSetHookManager updateEquippable(EquipmentSet equipmentSet, EquippableSetData equippableSetData, boolean takeEffect){
        boolean containsKey = activatedEquipped.containsKey(equipmentSet);

        if (takeEffect) {
            handleEquipEquipment(equipmentSet, equippableSetData, containsKey);
        } else {
            handleUnequipEquipment(equipmentSet, equippableSetData, containsKey);
        }
        return this;
    }

    private void handleEquipEquipment(EquipmentSet equipmentSet, EquippableSetData equippableSetData, boolean containsKey) {
        activatedEquipped.put(equipmentSet, equippableSetData);
    }

    private void handleUnequipEquipment(EquipmentSet equipmentSet, EquippableSetData equippableSetData, boolean containsKey) {
        if (!containsKey) {
            return;
        }
        activatedEquipped.remove(equipmentSet, equippableSetData);
    }


    public void sync(Player player) {
        player.setData(EBAttachments.ENTITY_HOOK_MANAGER, entityHookManager);
    }

    public Map<IBenediction, HookMap> getHooks() {
        Map<IBenediction, HookMap> result = new HashMap<>();
        activatedEquipped.asMap().forEach((equipmentSet, datas) -> {
            if (datas.isEmpty()) {
                return;
            }
            HookMap hookMap = null;
            for (EquippableSetData data : datas) {
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
        List<Map.Entry<EquipmentSet, EquippableSetData>> toRemove = new ArrayList<>();
        activatedEquipped.entries().forEach(entry -> {
            if (!entry.getValue().isValid(livingEntity)) {
                toRemove.add(entry);
            }
        });

        toRemove.forEach(entry -> {
            activatedEquipped.remove(entry.getKey(), entry.getValue());
        });
        
        return this;
    }
}
