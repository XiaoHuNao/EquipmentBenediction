package com.xiaohunao.equipment_benediction.common.manager;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.xiaohunao.equipment_benediction.common.event.EBRegisteredEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoader;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 通用的资源管理器基类
 */
public abstract class EBAbstractManager<T> extends SimpleJsonResourceReloadListener implements EBRegisteredEvent.EBRegistry<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(EBAbstractManager.class);
    
    protected final Map<ResourceLocation, T> staticResources = new HashMap<>();
    protected final Map<ResourceLocation, T> dynamicResources = new HashMap<>();
    protected final Set<ResourceLocation> expectedDynamicResources = new HashSet<>();
    protected final BiMap<ResourceLocation, T> allResources = HashBiMap.create();
    protected boolean seenRegisterEvent = false;

    protected EBAbstractManager(Gson gson, String folder) {
        super(gson, folder);
    }

    public void init(IEventBus modBus) {
        if (!seenRegisterEvent) {
            modBus.addListener(EventPriority.NORMAL, false, FMLCommonSetupEvent.class,
                    e -> e.enqueueWork(() -> {
                        ModLoader.postEvent(EBRegisteredEvent.create(this));
                        seenRegisterEvent = true;
                    }));

            NeoForge.EVENT_BUS.addListener(EventPriority.NORMAL, false,
                    AddReloadListenerEvent.class, event -> {
                        event.addListener(this);
                    });
        }
    }


    @Override
    public void registerExpected(ResourceLocation id) {
        if (seenRegisterEvent) {
            throw new IllegalStateException("Cannot register new entries after registration event has been fired.");
        }
        if (staticResources.containsKey(id)) {
            throw new IllegalArgumentException("Already registered as a static resource " + id);
        }
        expectedDynamicResources.add(id);
    }


    @Override
    public void registerStatic(ResourceLocation id, T value) {
        if (seenRegisterEvent) {
            throw new IllegalStateException("Cannot register new entries after registration event has been fired.");
        }
        if (expectedDynamicResources.contains(id)) {
            throw new IllegalArgumentException("Already registered as a dynamic resource " + id);
        }
        T original = staticResources.put(id, value);
        if (original != null) {
            throw new IllegalArgumentException("Duplicate static registration " + id);
        }
        allResources.put(id, value);
    }

    protected void registerDynamic(ResourceLocation id, T value) {
        if (!expectedDynamicResources.contains(id)) {
            LOGGER.warn("Unexpected dynamic resource: {}", id);
            return;
        }
        T original = dynamicResources.put(id, value);
        if (original != null) {
            throw new IllegalArgumentException("Duplicate dynamic registration " + id);
        }
        allResources.put(id, value);
    }


    protected void clearData() {
        expectedDynamicResources.forEach(allResources::remove);
        clearDynamicData();
    }

    protected boolean isDynamicResourceLoaded(ResourceLocation id) {
        return dynamicResources.containsKey(id);
    }

    public Map<ResourceLocation, T> getAllResources() {
        return Collections.unmodifiableMap(allResources);
    }

    public T getResource(ResourceLocation id) {
        T resource = allResources.get(id);
        if (resource == null) {
            throw new IllegalArgumentException("Resource not found: " + id);
        }
        return resource;
    }
    public ResourceLocation getResource(T resource){
        ResourceLocation location = allResources.inverse().get(resource);
        if (location == null) {
            throw new IllegalArgumentException("Resource not found: " + resource);
        }
        return location;
    }

    public boolean hasResource(ResourceLocation id) {
        return allResources.containsKey(id);
    }

    protected void clearDynamicData() {
        dynamicResources.clear();
    }

    public T getDynamicResource(ResourceLocation id) {
        if (!expectedDynamicResources.contains(id)) {
            throw new IllegalArgumentException("Resource not registered as dynamic: " + id);
        }
        if (isDynamicResourceLoaded(id)) {
            throw new IllegalStateException("Dynamic resource not loaded: " + id);
        }
        return dynamicResources.get(id);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        long startTime = System.nanoTime();
        seenRegisterEvent = true;
        
        clearData();
        loadData(pObject, pResourceManager, pProfiler);
        validateExpectedResources();
        
        long endTime = System.nanoTime();
        LOGGER.info("Loaded {} in {} ms", getManagerName(), (endTime - startTime) / 1000000f);
    }

    protected void loadData(Map<ResourceLocation, JsonElement> data, ResourceManager manager, ProfilerFiller profiler) {
        data.forEach((id, json) -> {
            try {
                if (expectedDynamicResources.contains(id)) {
                    loadDynamicResource(id, json, manager, profiler);
                } else {
                    LOGGER.warn("Skipping unexpected dynamic resource: {}", id);
                }
            } catch (Exception e) {
                LOGGER.error("Error loading dynamic resource {}: {}", id, e.getMessage());
            }
        });
    }

    protected void validateExpectedResources() {
        for (ResourceLocation id : expectedDynamicResources) {
            if (isDynamicResourceLoaded(id)) {
                LOGGER.warn("Missing expected dynamic resource: {}", id);
            }
        }
    }

    protected abstract void loadDynamicResource(ResourceLocation id, JsonElement json, ResourceManager manager, ProfilerFiller profiler);

    protected abstract String getManagerName();

}
