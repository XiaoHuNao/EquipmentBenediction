package com.xiaohunao.equipment_benediction.common.hook;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.xiaohunao.equipment_benediction.common.attachment.EntityHookManager;
import com.xiaohunao.equipment_benediction.common.init.EBAttachments;
import com.xiaohunao.equipment_benediction.common.interfaces.IBenediction;
import net.minecraft.world.entity.Entity;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class HookMapManager {
    private static final HookMapManager INSTANCE = new HookMapManager();
    private final Multimap<HookType<?>, IBenediction> hookRegistry = HashMultimap.create();
    private final BiMap<IBenediction, HookMap> hookMapRegistry = HashBiMap.create();
    private final BiMap<Integer, IHook> globalHooksById = HashBiMap.create();
    private final AtomicInteger nextId = new AtomicInteger(1);

    private HookMapManager() {}

    public static HookMapManager getInstance() {
        return INSTANCE;
    }

    public void register(IBenediction owner, HookMap hookMap) {
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

    public static <T extends IHook, R> void postHooks(HookType<T> hookType, HookExecutor<T, R> executor, Entity entity) {
        postHooks(hookType, executor, entity, null);
    }

    public static <T extends IHook, R> R postHooks(HookType<T> hookType, HookExecutor<T, R> executor, Entity entity, R defaultValue) {
        EntityHookManager entityHookManager = entity.getData(EBAttachments.ENTITY_HOOK_MANAGER);
        R result = defaultValue;
        DelayHookManager delayHookManager = DelayHookManager.getInstance();
        
        for (Map.Entry<IBenediction, HookMap> entry : entityHookManager.getHooks().entrySet()) {
            IBenediction owner = entry.getKey();
            HookMap hookMap = entry.getValue();
            if (hookMap.get(hookType).isEmpty()) {
                continue;
            }
            for (T hook : hookMap.get(hookType)) {
                if (hook instanceof DelayHook<?> delayHook) {
                    if (!delayHookManager.isDelayed(delayHook.getHook())){
                        delayHookManager.register(owner,delayHook,executor);
                    }
                } else {
                    R hookResult = executor.execute(owner, hook, result);
                    if (hookResult != null) {
                        result = hookResult;
                    }
                }
            }
        }
        return result;
    }

    @FunctionalInterface
    public interface HookExecutor<T extends IHook, R> {
        R execute(IBenediction owner, T hook, R original);
    }
}
