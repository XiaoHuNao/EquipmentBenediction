package com.xiaohunao.equipment_benediction.common.manager;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 通用的资源管理器基类
 */
public abstract class EBAbstractManager<T> extends SimpleJsonResourceReloadListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(EBAbstractManager.class);
    
    protected final Map<ResourceLocation, T> staticResources = new HashMap<>();
    protected final Set<ResourceLocation> expectedDynamicResources = new HashSet<>();
    // 统一存储所有资源的映射
    protected final Map<ResourceLocation, T> allResources = new HashMap<>();
    protected boolean seenRegisterEvent = false;

    protected EBAbstractManager(Gson gson, String folder) {
        super(gson, folder);
    }


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

    protected void addDynamicResource(ResourceLocation id, T value) {
        if (!expectedDynamicResources.contains(id)) {
            LOGGER.warn("Unexpected dynamic resource: {}", id);
            return;
        }
        allResources.put(id, value);
    }


    protected void clearData() {
        // 清除动态资源
        expectedDynamicResources.forEach(allResources::remove);
        clearDynamicData();
    }

    /**
     * 获取所有资源（包括静态和动态）
     */
    public Map<ResourceLocation, T> getAllResources() {
        return Collections.unmodifiableMap(allResources);
    }

    /**
     * 获取指定ID的资源（包括静态和动态）
     */
    public T getResource(ResourceLocation id) {
        T resource = allResources.get(id);
        if (resource == null) {
            throw new IllegalArgumentException("Resource not found: " + id);
        }
        return resource;
    }

    /**
     * 检查是否存在指定ID的资源（包括静态和动态）
     */
    public boolean hasResource(ResourceLocation id) {
        return allResources.containsKey(id);
    }

    // 子类需要实现的方法，用于清除特定的动态数据
    protected abstract void clearDynamicData();

    public void registerExpected(ResourceLocation id) {
        if (seenRegisterEvent) {
            throw new IllegalStateException("Cannot register new entries after registration event has been fired.");
        }
        if (staticResources.containsKey(id)) {
            throw new IllegalArgumentException("Already registered as a static resource " + id);
        }
        expectedDynamicResources.add(id);
    }

    public T getDynamicResource(ResourceLocation id) {
        if (!expectedDynamicResources.contains(id)) {
            throw new IllegalArgumentException("Resource not registered as dynamic: " + id);
        }
        if (!isDynamicResourceLoaded(id)) {
            throw new IllegalStateException("Dynamic resource not loaded: " + id);
        }
        return getDynamicResourceUnchecked(id);
    }

    public T getStaticResource(ResourceLocation id) {
        T resource = staticResources.get(id);
        if (resource == null) {
            throw new IllegalArgumentException("Static resource not found: " + id);
        }
        return resource;
    }

    protected abstract boolean isDynamicResourceLoaded(ResourceLocation id);
    protected abstract T getDynamicResourceUnchecked(ResourceLocation id);
    protected abstract String getManagerName();

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

    protected abstract void loadDynamicResource(ResourceLocation id, JsonElement json, ResourceManager manager, ProfilerFiller profiler);

    protected void validateExpectedResources() {
        for (ResourceLocation id : expectedDynamicResources) {
            if (!isDynamicResourceLoaded(id)) {
                LOGGER.warn("Missing expected dynamic resource: {}", id);
            }
        }
    }

    /**
     * 获取静态资源
     */
    protected Map<ResourceLocation, T> getStaticResources() {
        return staticResources;
    }

    /**
     * 获取预期的动态资源
     */
    protected Set<ResourceLocation> getExpectedDynamicResources() {
        return expectedDynamicResources;
    }
}
