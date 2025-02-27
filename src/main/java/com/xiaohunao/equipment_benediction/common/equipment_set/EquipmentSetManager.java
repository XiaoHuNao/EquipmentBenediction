package com.xiaohunao.equipment_benediction.common.equipment_set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.xiaohunao.equipment_benediction.common.context.LivingEquipmentChangeContext;
import com.xiaohunao.equipment_benediction.common.init.EBAttachments;
import com.xiaohunao.equipment_benediction.common.manager.EBAbstractManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
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

    protected EquipmentSetManager() {
        super(GSON, FOLDER);
    }

    public static EquipmentSetManager getInstance() {
        return INSTANCE;
    }

    public void updateSet(LivingEquipmentChangeContext livingEquipmentChangeContext) {
        ItemStack to = livingEquipmentChangeContext.to();
        ItemStack from = livingEquipmentChangeContext.from();
        LivingEntity livingEntity = livingEquipmentChangeContext.livingEntity();
        if (hasSet(from)) {
            getSet(from).forEach((set) -> {
                if (!set.isValid(livingEntity)) {
                    removePlayerSet(set, livingEntity);
                }
            });
        }
        if (hasSet(to)) {
            getSet(to).forEach((set) -> {
                if (set.isValid(livingEntity) && !hasSet(livingEntity, set)) {
                    addPlayerSet(set, livingEntity);
                }
            });
        }
    }

    private void removePlayerSet(EquipmentSet set, LivingEntity livingEntity) {
        livingEntity.getData(EBAttachments.ENTITY_HOOK_MANAGER).removeHookMap(set);
    }

    private void addPlayerSet(EquipmentSet equipmentSet, LivingEntity livingEntity) {
        livingEntity.setData(EBAttachments.ENTITY_HOOK_MANAGER, livingEntity.getData(EBAttachments.ENTITY_HOOK_MANAGER).addHookMap(equipmentSet, equipmentSet.hookMap));
    }

    private boolean hasSet(LivingEntity livingEntity, EquipmentSet equipmentSet) {
        return livingEntity.hasData(EBAttachments.ENTITY_HOOK_MANAGER) && livingEntity.getData(EBAttachments.ENTITY_HOOK_MANAGER).contains(equipmentSet);
    }

    public boolean hasSet(ItemStack stack) {
        return !getSet(stack).isEmpty();
    }

    public Collection<EquipmentSet> getSet(ItemStack stack) {
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
    protected void registerDynamic(ResourceLocation id, EquipmentSet value) {
        super.registerDynamic(id, value);
        for (Ingredient ingredient : value.equippableGroup.equipages.values()) {
            equipmentSetMap.put(value, ingredient);
        }
    }

    @Override
    public void registerStatic(ResourceLocation id, EquipmentSet value) {
        super.registerStatic(id, value);
        for (Ingredient ingredient : value.equippableGroup.equipages.values()) {
            equipmentSetMap.put(value, ingredient);
        }
    }
}
