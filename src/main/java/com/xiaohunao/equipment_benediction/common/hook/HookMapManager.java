package com.xiaohunao.equipment_benediction.common.hook;

import com.google.common.collect.*;
import com.xiaohunao.equipment_benediction.common.attachment.EntityHookManager;
import com.xiaohunao.equipment_benediction.common.init.EBAttachments;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.Collection;
import java.util.Map;

public class HookMapManager {
    private static final HookMapManager INSTANCE = new HookMapManager();
    private final Multimap<HookType<?>, Object> hookRegistry = HashMultimap.create();
    private final BiMap<Object,HookMap> hookMapRegistry = HashBiMap.create();

    private HookMapManager() {}

    public static HookMapManager getInstance() {
        return INSTANCE;
    }

    public void register(Object Owner, HookMap hookMap) {
        hookMap.hooks().forEach((key, value) -> hookRegistry.put(key, Owner));
        hookMapRegistry.put(Owner, hookMap);
    }

    public static <T extends IHook> void postHooks(HookType<T> hookType, HookExecutor<T> executor, Entity entity) {
        EntityHookManager entityHookManager = entity.getData(EBAttachments.ENTITY_HOOK_MANAGER);
        entityHookManager.getHooks().forEach((owner, hookMap) -> {
            if (hookMap.get(hookType).isEmpty()) {
                return;
            }
            hookMap.get(hookType).forEach(hook -> {
                executor.execute(owner,hook);
            });
        });
    }

    @FunctionalInterface
    public interface HookExecutor<T extends IHook> {
        void execute(Object owner,T hook);
    }
}
