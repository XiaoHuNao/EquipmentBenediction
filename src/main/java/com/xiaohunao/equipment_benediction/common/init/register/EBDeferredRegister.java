package com.xiaohunao.equipment_benediction.common.init.register;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.xiaohunao.equipment_benediction.common.event.EBRegisteredEvent;
import com.xiaohunao.equipment_benediction.common.interfaces.IBenediction;
import com.xiaohunao.equipment_benediction.api.manager.EBAbstractManager;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 通用的延迟注册器，类似于 DeferredRegister
 * @param <T> 要注册的对象类型
 */
public class EBDeferredRegister<T extends IBenediction> {
    private final String modid;
    private final EBAbstractManager<T> manager;
    private final Map<ResourceLocation, EBDeferredHolder<T>> entries = new LinkedHashMap<>();
    private boolean registered = false;

    private EBDeferredRegister(String modid, EBAbstractManager<T> manager) {
        this.modid = modid;
        this.manager = manager;
    }

    public static <T extends IBenediction> EBDeferredRegister<T> create(String modid, EBAbstractManager<T> manager) {
        return new EBDeferredRegister<>(modid, manager);
    }

    public EBStaticHolder<T> register(String name, Supplier<? extends T> supplier) {
        if (registered) {
            throw new IllegalStateException("Cannot register after registration event was fired");
        }
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(modid, name);
        if (entries.containsKey(id)) {
            throw new IllegalArgumentException("Duplicate registration: " + id);
        }
        EBStaticHolder<T> holder = new EBStaticHolder<>(id, supplier);
        entries.put(id, holder);
        return holder;
    }

    public EBDynamicHolder<T> registerDynamic(String name) {
        if (registered) {
            throw new IllegalStateException("Cannot register after registration event was fired");
        }
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(modid, name);
        if (entries.containsKey(id)) {
            throw new IllegalArgumentException("Duplicate registration: " + id);
        }
        EBDynamicHolder<T> holder = new EBDynamicHolder<>(id, manager);
        entries.put(id, holder);
        return holder;
    }

    public void register(IEventBus bus) {
        if (!registered) {
            bus.addListener(this::onRegister);
            registered = true;
        }
    }

    private void onRegister(EBRegisteredEvent<T> event) {
        if(!event.isValidManager(manager)){
            return;
        }
        entries.forEach((id, holder) -> {
            if (holder instanceof EBStaticHolder<T> staticHolder) {
                event.registerStatic(id, staticHolder.get());
            } else if (holder instanceof EBDynamicHolder<T>) {
                event.registerExpected(id);
            }
        });
    }

    public EBAbstractManager<T> getManager() {
        return manager;
    }

    public Map<ResourceLocation, Supplier<T>> getEntries() {
        return ImmutableMap.copyOf(entries);
    }

    public Codec<T> byNameCodec() {
        return ResourceLocation.CODEC.xmap(location -> getManager().getResource(location), t -> getManager().getResource(t));
    }
}
