package com.xiaohunao.equipment_benediction.common.hook;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.util.Collection;

public class HookMapManager {
    private static final HookMapManager INSTANCE = new HookMapManager();
    private final BiMap<Object, HookMap> hookMapRegistry = HashBiMap.create();

    private HookMapManager() {}

    public static HookMapManager getInstance() {
        return INSTANCE;
    }

    public void register(Object owner, HookMap hookMap) {
        hookMapRegistry.put(owner, hookMap);
    }

    public HookMap getHookMap(Object owner) {
        return hookMapRegistry.get(owner);
    }

    public static <T extends IHook> void postHooks(HookType<T> hookType, HookExecutor<T> executor) {
        HookMapManager manager = getInstance();
        for (HookMap hookMap : manager.hookMapRegistry.values()) {
            Collection<T> hooks = hookMap.get(hookType);
            for (T hook : hooks) {
                executor.execute(hook);
            }
        }
    }

    @FunctionalInterface
    public interface HookExecutor<T extends IHook> {
        void execute(T hook);
    }
}
