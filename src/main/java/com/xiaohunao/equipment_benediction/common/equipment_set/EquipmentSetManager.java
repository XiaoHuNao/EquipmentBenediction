package com.xiaohunao.equipment_benediction.common.equipment_set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.xiaohunao.equipment_benediction.common.manager.EBAbstractManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.Ingredient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EquipmentSetManager extends EBAbstractManager<EquipmentSet> {
    private static final Logger LOGGER = LoggerFactory.getLogger(EquipmentSetManager.class);
    private static final Gson GSON = new Gson();
    public static final String FOLDER = "equipment_set";
    private static final EquipmentSetManager INSTANCE = new EquipmentSetManager();

    private static final Multimap<Ingredient,EquipmentSet> equipmentSetMap = HashMultimap.create();

    protected EquipmentSetManager() {
        super(GSON, FOLDER);
    }

    public static EquipmentSetManager getInstance() {
        return INSTANCE;
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
            equipmentSetMap.put(ingredient, value);
        }
    }

    @Override
    public void registerStatic(ResourceLocation id, EquipmentSet value) {
        super.registerStatic(id, value);
        for (Ingredient ingredient : value.equippableGroup.equipages.values()) {
            equipmentSetMap.put(ingredient, value);
        }
    }
}
