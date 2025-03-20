package com.xiaohunao.equipment_benediction.common.hook;

import com.google.common.collect.*;
import com.xiaohunao.equipment_benediction.common.hook.dynamic.ISerializableHook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class HookMap {


    private static final Logger LOGGER = LoggerFactory.getLogger(HookMap.class);
    private final Multimap<HookType<?>, IHook> hooks;

    private HookMap(Multimap<HookType<?>, IHook> hooks, BiMap<Integer, IHook> hooksById) {
        this.hooks = ImmutableMultimap.copyOf(hooks);
    }

    @SuppressWarnings("unchecked")
    public <T extends IHook> Collection<T> get(HookType<T> type) {
        Set<T> result = new HashSet<>();
        hooks.forEach((hookType, hook) -> {
            if (type.getHookClass().isAssignableFrom(hook.getClass())) {
                result.add((T) hook);
            }
        });
        return result;
    }

    public <T extends IHook> Collection<T> get(Supplier<HookType<T>> type) {
        return get(type.get());
    }

    public boolean isEmpty() {
        return hooks.isEmpty();
    }

    public static Builder builder() {
        return new Builder();
    }

    public Multimap<HookType<?>, IHook> hooks() {
        return hooks;
    }

    public List<ISerializableHook> getSerializableHooks() {
        List<ISerializableHook> serializableHooks = new ArrayList<>();
        for (HookType<?> type : hooks.keySet()) {
            if (type.isSerializable()) {
                hooks.get(type).stream()
                        .filter(hook -> hook instanceof ISerializableHook)
                        .map(hook -> (ISerializableHook) hook)
                        .forEach(serializableHooks::add);
            }
        }
        return serializableHooks;
    }

    public HookMap merge(HookMap hookMap) {
        Builder builder = new Builder();

        for (Map.Entry<HookType<?>, IHook> entry : hooks.entries()) {
            builder.addHook(entry.getKey(), entry.getValue());
        }

        for (Map.Entry<HookType<?>, IHook> entry : hookMap.hooks.entries()) {
            builder.addHook(entry.getKey(), entry.getValue());
        }

        return builder.build();
    }

    public HookMap deduplicate(HookMap hookMap) {
        Builder builder = new Builder();

        for (Map.Entry<HookType<?>, IHook> entry : hookMap.hooks.entries()) {
            if (!hooks.containsEntry(entry.getKey(), entry.getValue())) {
                builder.addHook(entry.getKey(), entry.getValue());
            }
        }

        return builder.build();
    }

    public boolean contentEquals(HookMap other) {
        if (this == other) return true;
        if (other == null) return false;

        if (hooks.size() != other.hooks.size()) return false;

        for (HookType<?> type : hooks.keySet()) {
            Collection<IHook> thisHooks = hooks.get(type);
            Collection<IHook> otherHooks = other.hooks.get(type);

            if (thisHooks.size() != otherHooks.size()) return false;

            for (IHook hook : thisHooks) {
                boolean found = false;
                for (IHook otherHook : otherHooks) {
                    if (hook.equals(otherHook)) {
                        found = true;
                        break;
                    }
                }
                if (!found) return false;
            }
        }

        return true;
    }


//    public static final Codec<HookMap> CODEC = RecordCodecBuilder.create(instance -> instance.group(
//        Codec.unboundedMap(
//            ResourceLocation.CODEC,
//            Codec.list(Codec.INT)
//        ).fieldOf("hooks").forGetter(hookMap -> {
//            Map<ResourceLocation, List<Integer>> result = new HashMap<>();
//            HookMapManager manager = HookMapManager.getInstance();
//
//            hookMap.hooks.asMap().forEach((hookType, hooks) -> {
//                ResourceLocation typeId = EBRegistries.Suppliers.HOOK_TYPES.get().getKey(hookType);
//                if (typeId == null){
//                    LOGGER.error("Hook type {} is not registered", hookType);
//                }
//
//                hooks.forEach(hook -> {
//                    Integer id = manager.getIdForHook(hook);
//                    if (result.containsKey(typeId)) {
//                        result.get(typeId).add(id);
//                    }else {
//                        result.put(typeId,Lists.newArrayList(id));
//                    }
//                });
//
//            });
//
//            return result.isEmpty() ? Collections.emptyMap() : result;
//        })
//    ).apply(instance, map -> {
//        Builder builder = HookMap.builder();
//        HookMapManager manager = HookMapManager.getInstance();
//
//        if (map.isEmpty()) {
//            LOGGER.error("Hook map: {} is empty", map);
//            return builder.build();
//        }
//
//        map.forEach((typeId, hookIds) -> {
//            HookType<?> hookType = EBRegistries.Suppliers.HOOK_TYPES.get().get(typeId);
//            if (hookType == null) {
//                LOGGER.error("Hook type {} is not registered", typeId);
//                return;
//            }
//            hookIds.forEach(hookId -> {
//                IHook hook = manager.getHookById(hookId);
//                if (hook == null) {
//                    LOGGER.error("Hook {} is not registered", hookId);
//                    return;
//                }
//                builder.addHook(hookType, hook);
//            });
//        });
//
//        return builder.build();
//    }));

    public static class Builder {
        private final Multimap<HookType<?>, IHook> hooks = HashMultimap.create();
        private final BiMap<Integer, IHook> hooksById = HashBiMap.create();
        private final AtomicInteger nextId = new AtomicInteger(1);

        public <T extends IHook> Builder addHook(HookType<T> type, T hook) {
            hooks.put(type, hook);
            if (!hooksById.inverse().containsKey(hook)) {
                int id = nextId.getAndIncrement();
                hooksById.put(id, hook);
            }
            return this;
        }

        public Builder addHook(HookType<?> type, IHook... hookCollection) {
            for (IHook hook : hookCollection) {
                hooks.put(type, hook);
                if (!hooksById.inverse().containsKey(hook)) {
                    int id = nextId.getAndIncrement();
                    hooksById.put(id, hook);
                }
            }
            return this;
        }

        public HookMap build() {
            return new HookMap(hooks, hooksById);
        }
    }


}
