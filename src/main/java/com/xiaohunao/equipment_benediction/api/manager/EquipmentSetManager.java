package com.xiaohunao.equipment_benediction.api.manager;

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
import com.xiaohunao.equipment_benediction.common.network.EntityHookManagerSyncPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
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
import java.util.Map;

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
//        LivingEntity livingEntity = livingEquipmentChangeContext.livingEntity();
//        if (livingEntity.level().isClientSide){
//            return;
//        }
//        ItemStack to = livingEquipmentChangeContext.to();
//        ItemStack from = livingEquipmentChangeContext.from();
//
//        EntityHookManager hookManager = livingEntity.getData(EBAttachments.ENTITY_HOOK_MANAGER);
//        Multimap<EquipmentSet, EquippableSetData> equipmentSetDataHookMap = hookManager.getEquipmentSetDataHookMap();
//
//        // 创建一个列表来存储需要移除的数据，避免并发修改异常
//        List<Map.Entry<EquipmentSet, EquippableSetData>> toRemove = Lists.newArrayList();
//
//        // 检查并收集无效的套装数据
//        equipmentSetDataHookMap.entries().forEach(entry -> {
//            if (!entry.getValue().isValid(livingEntity)) {
//                toRemove.add(entry);
//            }
//        });
//
//        // 移除无效的套装数据
//        toRemove.forEach(entry -> {
//            hookManager.updateEquippableSetData(entry.getKey(), entry.getValue(), false);
//        });
//
//        // 处理新添加的装备
//        if (hasEquipmentSet(to)) {
//            getEquipmentSet(to).forEach(set -> {
//                // 获取该套装的所有可装备数据
//                Collection<EquippableSetData> setDataCollection = equipmentSetDataMap.get(set);
//
//                // 获取该套装当前已激活的数据
//                Collection<EquippableSetData> activeSetData = equipmentSetDataHookMap.get(set);
//
//                // 检查每个可装备数据
//                for (EquippableSetData setData : setDataCollection) {
//                    // 检查该数据是否有效
//                    if (setData.isValid(livingEntity)) {
//                        boolean isExclusive = set.getEquippableGroup().isExclusive(setData);
//
//                        // 如果是独占的，需要先移除该套装的所有已激活数据
//                        if (isExclusive && !activeSetData.isEmpty()) {
//                            // 移除该套装的所有已激活数据
//                            for (EquippableSetData activeData : Lists.newArrayList(activeSetData)) {
//                                hookManager.updateEquippableSetData(set, activeData, false);
//                            }
//                            // 添加新的数据
//                            hookManager.updateEquippableSetData(set, setData, true);
//                        }
//                        // 如果不是独占的，或者当前没有激活的数据
//                        else if (!isExclusive || activeSetData.isEmpty()) {
//                            // 检查是否已经激活了独占的数据
//                            boolean hasExclusiveActive = false;
//                            for (EquippableSetData activeData : activeSetData) {
//                                if (set.getEquippableGroup().isExclusive(activeData)) {
//                                    hasExclusiveActive = true;
//                                    break;
//                                }
//                            }
//
//                            // 如果没有激活独占数据，或者当前数据是独占的
//                            if (!hasExclusiveActive || isExclusive) {
//                                // 如果当前数据不在激活列表中，则添加
//                                if (!activeSetData.contains(setData)) {
//                                    hookManager.updateEquippableSetData(set, setData, true);
//                                }
//                            }
//                        }
//                    }
//                }
//            });
//        }
//
//        // 同步数据到客户端
//        if (livingEntity instanceof ServerPlayer serverPlayer) {
//            PacketDistributor.sendToPlayer(serverPlayer, new EntityHookManagerSyncPayload(livingEntity.getId(), hookManager.serializeNBT(null)));
//        }
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
