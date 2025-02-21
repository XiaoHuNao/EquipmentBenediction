package com.xiaohunao.equipment_benediction.common.manager;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import com.xiaohunao.equipment_benediction.common.hook.dynamic.ISerializableHook;
import com.xiaohunao.equipment_benediction.common.modifier.IManager;
import com.xiaohunao.equipment_benediction.common.modifier.Modifier;
import com.xiaohunao.equipment_benediction.common.modifier.SerializableModifier;
import com.xiaohunao.equipment_benediction.common.event.EBRegisteredEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoader;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class ModifierManager extends EBAbstractManager<Modifier> implements EBRegisteredEvent.EBRegistry<Modifier>, IManager<Modifier> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ModifierManager.class);
    private static final Gson GSON = new Gson();
    public static final String FOLDER = "modifiers";
    private static final ModifierManager INSTANCE = new ModifierManager();

    private final Map<ResourceLocation, SerializableModifier> dynamicModifiers = new HashMap<>();

    private ModifierManager() {
        super(GSON, FOLDER);
    }

    public static ModifierManager getInstance() {
        return INSTANCE;
    }

    /** 仅供内部使用 */
    public void init(IEventBus modBus) {
        if (!seenRegisterEvent) {
            modBus.addListener(EventPriority.NORMAL, false, FMLCommonSetupEvent.class, 
                e -> e.enqueueWork(this::fireRegistryEvent));
            NeoForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, 
                AddReloadListenerEvent.class, this::addDataPackListeners);
        }
    }

    /** 触发修饰器注册事件 */
    private void fireRegistryEvent() {
        EBRegisteredEvent<Modifier> event = EBRegisteredEvent.create(this);
        ModLoader.postEvent(event);
        seenRegisterEvent = true;
    }

    /** 添加数据包监听器 */
    private void addDataPackListeners(final AddReloadListenerEvent event) {
        event.addListener(this);
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
                dynamicModifiers.put(id, modifier);
                addDynamicResource(id, modifier);
            });
    }

    @Override
    protected boolean isDynamicResourceLoaded(ResourceLocation id) {
        return dynamicModifiers.containsKey(id);
    }

    @Override
    protected Modifier getDynamicResourceUnchecked(ResourceLocation id) {
        return dynamicModifiers.get(id);
    }

    @Override
    protected String getManagerName() {
        return "Modifiers";
    }

    @Override
    protected void clearDynamicData() {
        dynamicModifiers.clear();
    }

    public Collection<ResourceLocation> getStaticModifierIds() {
        return Collections.unmodifiableSet(staticResources.keySet());
    }

    public Collection<ResourceLocation> getDynamicModifierIds() {
        return Collections.unmodifiableSet(dynamicModifiers.keySet());
    }

    public Collection<ResourceLocation> getExpectedDynamicModifierIds() {
        return Collections.unmodifiableSet(expectedDynamicResources);
    }

    @Override
    public Modifier getDynamicResource(ResourceLocation id) {
        return dynamicModifiers.get(id);
    }

    public Map<ResourceLocation, SerializableModifier> getModifiers() {
        return dynamicModifiers;
    }

    @Override
    public void register(ResourceLocation id, Modifier value) {
        if (value == null) {
            throw new IllegalArgumentException("Cannot register null modifier: " + id);
        }
        registerStatic(id, value);
    }

    @Override
    public void registerExpected(ResourceLocation id) {
        super.registerExpected(id);
    }

    @Override
    public EBAbstractManager<Modifier> getManager() {
        return this;
    }

    @Override
    public void registerStatic(ResourceLocation id, Modifier value) {
        if (value == null) {
            throw new IllegalArgumentException("Cannot register null modifier: " + id);
        }
        super.registerStatic(id, value);
        if (value instanceof SerializableModifier serializableModifier) {
            addDynamicResource(id, serializableModifier);
        }
    }

    /**
     * 获取所有修饰器（包括静态和动态）
     */
    public Map<ResourceLocation, Modifier> getAllModifiers() {
        return Collections.unmodifiableMap(allResources);
    }

    /**
     * 获取指定ID的修饰器（包括静态和动态）
     */
    public Modifier getModifier(ResourceLocation id) {
        Modifier modifier = allResources.get(id);
        if (modifier == null) {
            throw new IllegalArgumentException("Modifier not found: " + id);
        }
        return modifier;
    }

    /**
     * 检查是否存在指定ID的修饰器（包括静态和动态）
     */
    public boolean hasModifier(ResourceLocation id) {
        return allResources.containsKey(id);
    }
}
