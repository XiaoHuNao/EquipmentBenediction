package com.xiaohunao.equipment_benediction.api;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.xiaohunao.equipment_benediction.common.attachment.EntityHookManager;
import com.xiaohunao.equipment_benediction.common.context.LivingEquipmentChangeContext;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSet;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquippableSetData;
import com.xiaohunao.equipment_benediction.common.init.EBAttachments;
import com.xiaohunao.equipment_benediction.common.manager.EBAbstractManager;
import com.xiaohunao.equipment_benediction.common.network.EntityHookManagerSyncPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.network.PacketDistributor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;

public class EquipmentSetManager extends EBAbstractManager<EquipmentSet> {
    private static final Logger LOGGER = LoggerFactory.getLogger(EquipmentSetManager.class);
    private static final Gson GSON = new Gson();
    public static final String FOLDER = "equipment_set";
    private static final EquipmentSetManager INSTANCE = new EquipmentSetManager();

    private static final Multimap<EquipmentSet, Ingredient> equipmentSetMap = HashMultimap.create();
    private static final Multimap<EquipmentSet, EquippableSetData> equipmentSetDataMap = HashMultimap.create();


    protected EquipmentSetManager() {
        super(GSON, FOLDER);
    }

    public static EquipmentSetManager getInstance() {
        return INSTANCE;
    }

    public void updateSet(LivingEquipmentChangeContext livingEquipmentChangeContext) {
        LivingEntity livingEntity = livingEquipmentChangeContext.livingEntity();
        if (livingEntity.level().isClientSide){
            return;
        }
        ItemStack to = livingEquipmentChangeContext.to();
        ItemStack from = livingEquipmentChangeContext.from();

        if (hasEquipmentSet(from)) {
            getEquipmentSet(from).forEach((set) -> {
                EquippableSetData equippableSetData = set.getEquippableSet(livingEntity);
                if (equippableSetData == null) {
                    removePlayerSet(set, livingEntity);
                }
            });
        }
        if (hasEquipmentSet(to)) {
            getEquipmentSet(to).forEach((set) -> {
                EquippableSetData equippableSetData = set.getEquippableSet(livingEntity);
                if (equippableSetData != null) {
                    addPlayerSet(set, livingEntity);
                }
            });
        }
    }

    private void removePlayerSet(EquipmentSet equipmentSet, LivingEntity livingEntity) {
        EntityHookManager entityHookManager = livingEntity.getData(EBAttachments.ENTITY_HOOK_MANAGER).removeHookMap(equipmentSet);
        livingEntity.setData(EBAttachments.ENTITY_HOOK_MANAGER, entityHookManager);
        PacketDistributor.sendToAllPlayers(new EntityHookManagerSyncPayload(livingEntity.getId(),entityHookManager.serializeNBT(null)));
    }

    private void addPlayerSet(EquipmentSet equipmentSet, LivingEntity livingEntity) {
        EntityHookManager entityHookManager = livingEntity.getData(EBAttachments.ENTITY_HOOK_MANAGER).addHookMap(equipmentSet, equipmentSet.getHookMap(livingEntity));
        livingEntity.setData(EBAttachments.ENTITY_HOOK_MANAGER, entityHookManager);
        PacketDistributor.sendToAllPlayers(new EntityHookManagerSyncPayload(livingEntity.getId(),entityHookManager.serializeNBT(null)));
    }

    private boolean hasEquipmentSet(LivingEntity livingEntity, EquipmentSet equipmentSet) {
        return livingEntity.hasData(EBAttachments.ENTITY_HOOK_MANAGER) && livingEntity.getData(EBAttachments.ENTITY_HOOK_MANAGER).contains(equipmentSet);
    }

    public boolean hasEquipmentSet(ItemStack stack) {
        return !getEquipmentSet(stack).isEmpty();
    }


    public Collection<EquipmentSet> getEquipmentSet(ItemStack stack) {
        List<EquipmentSet> sets = Lists.newArrayList();
        equipmentSetMap.entries().forEach(entry -> {
            if (entry.getValue().test(stack)) {
                sets.add(entry.getKey());
            }
        });
        return ImmutableList.copyOf(sets);
    }

    @Override
    protected String getManagerName() {
        return "EquipmentSet";
    }

    @Override
    protected void loadDynamicResource(ResourceLocation id, JsonElement json, ResourceManager manager, ProfilerFiller profiler) {
    }

    @Override
    protected void registerDynamic(ResourceLocation id, EquipmentSet equipmentSet) {
        super.registerDynamic(id, equipmentSet);
        registerEquipmentSet(equipmentSet);
    }

    @Override
    public void registerStatic(ResourceLocation id, EquipmentSet equipmentSet) {
        super.registerStatic(id, equipmentSet);
        registerEquipmentSet(equipmentSet);
    }

    private void registerEquipmentSet(EquipmentSet set){
        for (EquippableSetData setData : set.getEquippableGroup().getEquippableSets()) {
            setData.equipages().values().forEach(ingredient -> {
                if (!ingredient.isEmpty()) {
                    equipmentSetMap.put(set, ingredient);
                }
            });
            equipmentSetDataMap.put(set, setData);
        }
    }}
