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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EquipmentSetHookManager implements INBTSerializable<CompoundTag> {
    private static final Logger LOGGER = LoggerFactory.getLogger(EquipmentSetHookManager.class);
    private final EntityHookManager entityHookManager;
    private final BenedictionManager benedictionManager = BenedictionManager.getInstance();

    //post
    private final BiMap<EquipmentSet, HookMap> equippedHooks = HashBiMap.create();
    //激活的
    private final Multimap<EquipmentSet, EquippableSetData>  activatedEquipped = HashMultimap.create();
    //选中的
    private final Multimap<EquipmentSet, EquippableSetData>  selectedEquipped = HashMultimap.create();

    public EquipmentSetHookManager(EntityHookManager entityHookManager) {
        this.entityHookManager = entityHookManager;
    }

    public BiMap<EquipmentSet, HookMap> getEquippedHooks() {
        return equippedHooks;
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

        CompoundTag equippedHooksTag = new CompoundTag();
        equippedHooks.forEach((equipmentSet, hookMap) -> {
            ResourceLocation setId = benedictionManager.getBenedictionManagerId(equipmentSet);
            equippedHooksTag.put(setId.toString(), HookMap.CODEC.encodeStart(NbtOps.INSTANCE, hookMap).getOrThrow());
        });

        CompoundTag activatedEquippedTag = serializeEquippedData(activatedEquipped);

        CompoundTag selectedEquippedTag = serializeEquippedData(selectedEquipped);
        
        compoundTag.put("equipped_hooks", equippedHooksTag);
        compoundTag.put("activated_equipped", activatedEquippedTag);
        compoundTag.put("selected_equipped", selectedEquippedTag);
        return compoundTag;
    }

    @Override
    public void deserializeNBT(@NotNull HolderLookup.Provider provider, @NotNull CompoundTag compoundTag) {
        equippedHooks.clear();
        activatedEquipped.clear();
        selectedEquipped.clear();

        if (compoundTag.contains("equipped_hooks")) {
            CompoundTag equippedHooksTag = compoundTag.getCompound("equipped_hooks");
            deserializeEquippedHooks(equippedHooksTag);
        }

        if (compoundTag.contains("activated_equipped")) {
            CompoundTag activatedTag = compoundTag.getCompound("activated_equipped");
            deserializeEquippedData(activatedTag, activatedEquipped);
        }

        if (compoundTag.contains("selected_equipped")) {
            CompoundTag selectedTag = compoundTag.getCompound("selected_equipped");
            deserializeEquippedData(selectedTag, selectedEquipped);
        }
    }

    private CompoundTag serializeEquippedData(Multimap<EquipmentSet, EquippableSetData> data) {
        CompoundTag tag = new CompoundTag();
        data.asMap().forEach((equipmentSet, equippableSetData) -> {
            ResourceLocation setId = benedictionManager.getBenedictionManagerId(equipmentSet);
            ListTag setDataListTag = new ListTag();
            equippableSetData.forEach(setData -> {
                EquippableSetData.CODEC.encodeStart(NbtOps.INSTANCE, setData).result().ifPresent(tag1 -> {
                    setDataListTag.add(tag1);
                });
            });
            tag.put(setId.toString(), setDataListTag);
        });
        return tag;
    }

    private void deserializeEquippedHooks(CompoundTag hooksTag) {
        LOGGER.debug("Deserializing equipped hooks from tag: {}", hooksTag);
        hooksTag.getAllKeys().forEach(key -> {
            ResourceLocation setId = ResourceLocation.tryParse(key);
            Tag tag = hooksTag.get(key);
            LOGGER.debug("Deserializing HookMap for {}, tag: {}", key, tag);
            HookMap hookMap = HookMap.CODEC.parse(NbtOps.INSTANCE, tag).getOrThrow();
            LOGGER.debug("Deserialized HookMap: {}", hookMap);
            IBenediction<?> benediction = benedictionManager.getBenedictionByManagerId(setId);
            if (benediction instanceof EquipmentSet equipmentSet) {
                equippedHooks.put(equipmentSet, hookMap);
                entityHookManager.addHookMap(benediction, hookMap);
            }
        });
    }

    private void deserializeEquippedData(CompoundTag tag, Multimap<EquipmentSet, EquippableSetData> target) {
        tag.getAllKeys().forEach(key -> {
            ResourceLocation setId = ResourceLocation.tryParse(key);
            if (setId != null) {
                IBenediction<?> benediction = benedictionManager.getBenedictionByManagerId(setId);
                if (benediction instanceof EquipmentSet equipmentSet) {
                    ListTag listTag = tag.getList(key, Tag.TAG_COMPOUND);
                    listTag.forEach(element -> {
                        target.put(equipmentSet, EquippableSetData.CODEC.decode(NbtOps.INSTANCE, element).getOrThrow().getFirst());
                    });
                }
            }
        });
    }

    public void updateSelectedEquipment(EquipmentSet equipmentSet, EquippableSetData equippableSetData, boolean takeEffect){
        if (takeEffect) {
            selectedEquipped.put(equipmentSet, equippableSetData);
            updateEquippable(equipmentSet, equippableSetData, true);
        }else {
            selectedEquipped.remove(equipmentSet, equippableSetData);
            updateEquippable(equipmentSet, equippableSetData, false);
        }
    }


    public void updateEquippable(EquipmentSet equipmentSet, EquippableSetData equippableSetData, boolean takeEffect){
        boolean containsKey = activatedEquipped.containsKey(equipmentSet);

        if (takeEffect) {
            handleEquipEquipment(equipmentSet, equippableSetData, containsKey);
        } else {
            handleUnequipEquipment(equipmentSet, equippableSetData, containsKey);
        }
    }


    private void handleEquipEquipment(EquipmentSet equipmentSet, EquippableSetData equippableSetData, boolean containsKey) {
        activatedEquipped.put(equipmentSet, equippableSetData);

        if (!containsKey) {
            HookMap newHookMap = equippableSetData.getHookMap();
            updateHookMaps(equipmentSet, newHookMap);
        } else {
            mergeExistingHooks(equipmentSet, equippableSetData);
        }
    }

    private void handleUnequipEquipment(EquipmentSet equipmentSet, EquippableSetData equippableSetData, boolean containsKey) {
        if (!containsKey) {
            return;
        }
        activatedEquipped.remove(equipmentSet, equippableSetData);

        HookMap existingHooks = entityHookManager.getHookMap(equipmentSet);
        if (existingHooks == null) {
            return;
        }
        deduplicatedHooks(equipmentSet, equippableSetData, existingHooks);

    }

    private void deduplicatedHooks(EquipmentSet equipmentSet, EquippableSetData equippableSetData, HookMap existingHooks) {
        HookMap hookMapToRemove = equippableSetData.getHookMap();
        if (hookMapToRemove != null) {
            HookMap deduplicatedHooks = existingHooks.deduplicate(hookMapToRemove);
            if (deduplicatedHooks.isEmpty()) {
                removeHookMaps(equipmentSet);
            } else {
                updateHookMaps(equipmentSet, deduplicatedHooks);
            }
        }
    }

    private void mergeExistingHooks(EquipmentSet equipmentSet, EquippableSetData equippableSetData) {
        HookMap existingHooks = entityHookManager.getHookMap(equipmentSet);
        if (existingHooks != null) {
            HookMap mergedHooks = existingHooks.merge(equippableSetData.getHookMap());
            updateHookMaps(equipmentSet, mergedHooks);
        }
    }


    private void updateHookMaps(EquipmentSet equipmentSet, HookMap hookMap) {
        entityHookManager.addHookMap(equipmentSet, hookMap);
        equippedHooks.put(equipmentSet, hookMap);
    }

    private void removeHookMaps(EquipmentSet equipmentSet){
        entityHookManager.removeHookMap(equipmentSet);
        equippedHooks.remove(equipmentSet);
    }
}
