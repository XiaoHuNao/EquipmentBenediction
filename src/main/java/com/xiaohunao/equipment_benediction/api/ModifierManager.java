package com.xiaohunao.equipment_benediction.api;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;

import com.xiaohunao.equipment_benediction.common.manager.EBAbstractManager;
import com.xiaohunao.equipment_benediction.common.modifier.Modifier;
import com.xiaohunao.equipment_benediction.common.modifier.SerializableModifier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModifierManager extends EBAbstractManager<Modifier> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ModifierManager.class);
    private static final Gson GSON = new Gson();
    public static final String FOLDER = "modifiers";
    private static final ModifierManager INSTANCE = new ModifierManager();


    private ModifierManager() {
        super(GSON, FOLDER);
    }

    public static ModifierManager getInstance() {
        return INSTANCE;
    }

    @Override
    protected void loadDynamicResource(ResourceLocation id, JsonElement json, ResourceManager manager, ProfilerFiller profiler) {
        if (!json.isJsonObject()) {
            LOGGER.error("Failed to parse modifier {}: Not a JSON object", id);
            return;
        }

        SerializableModifier.CODEC.parse(JsonOps.INSTANCE, json)
            .resultOrPartial(error -> LOGGER.error("Failed to parse modifier {}: {}", id, error))
            .ifPresent(modifier -> {
                registerDynamic(id, modifier);
            });
    }



    @Override
    protected String getManagerName() {
        return "Modifiers";
    }


}
