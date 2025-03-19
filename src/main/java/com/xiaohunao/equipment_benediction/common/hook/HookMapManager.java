package com.xiaohunao.equipment_benediction.common.hook;

import com.google.common.collect.*;
import com.xiaohunao.equipment_benediction.common.attachment.EntityHookManager;
import com.xiaohunao.equipment_benediction.common.init.EBAttachments;
import com.xiaohunao.equipment_benediction.common.interfaces.IBenediction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class HookMapManager {
    private static final HookMapManager INSTANCE = new HookMapManager();
    private final Multimap<HookType<?>, Object> hookRegistry = HashMultimap.create();
    private final BiMap<Object, HookMap> hookMapRegistry = HashBiMap.create();
    private final BiMap<Integer, IHook> globalHooksById = HashBiMap.create();
    private final AtomicInteger nextId = new AtomicInteger(1);

    private HookMapManager() {}

    public static HookMapManager getInstance() {
        return INSTANCE;
    }

    public void register(Object owner, HookMap hookMap) {
        hookMap.hooks().forEach((key, value) -> hookRegistry.put(key, owner));
        hookMapRegistry.put(owner, hookMap);

        for (HookType<?> type : hookMap.hooks().keySet()) {
            for (IHook hook : hookMap.hooks().get(type)) {
                if (!globalHooksById.inverse().containsKey(hook)) {
                    int id = nextId.getAndIncrement();
                    globalHooksById.put(id, hook);
                }
            }
        }
    }
    
    public IHook getHookById(int id) {
        return globalHooksById.get(id);
    }
    
    public Integer getIdForHook(IHook hook) {
        return globalHooksById.inverse().get(hook);
    }
    


    public static <T extends IHook, R> R postHooks(HookType<T> hookType, HookExecutor<T, R> executor, Entity entity) {
        EntityHookManager entityHookManager = entity.getData(EBAttachments.ENTITY_HOOK_MANAGER);
        R result = null;
        for (Map.Entry<IBenediction, HookMap> entry : entityHookManager.getHooks().entrySet()) {
            Object owner = entry.getKey();
            HookMap hookMap = entry.getValue();
            if (hookMap.get(hookType).isEmpty()) {
                continue;
            }
            for (T hook : hookMap.get(hookType)) {
                R hookResult = executor.execute(owner, hook);
                if (hookResult != null) {
                    result = hookResult;
                }
            }
        }
        return result;
    }

    @FunctionalInterface
    public interface HookExecutor<T extends IHook, R> {
        R execute(Object owner, T hook);
    }
}
