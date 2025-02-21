package com.xiaohunao.equipment_benediction.common.hook;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import com.xiaohunao.equipment_benediction.common.hook.dynamic.ISerializableHook;

public class HookMap {
    private final Multimap<HookType<?>, IHook> hooks;

    private HookMap(Multimap<HookType<?>, IHook> hooks) {
        this.hooks = ImmutableMultimap.copyOf(hooks);
    }

    @SuppressWarnings("unchecked")
    public <T extends IHook> Collection<T> getHooks(HookType<T> type) {
        return (Collection<T>) hooks.get(type);
    }

    public boolean isEmpty() {
        return hooks.isEmpty();
    }

    public static Builder builder() {
        return new Builder();
    }

    public Collection<HookType<?>> getTypes() {
        return hooks.keySet();
    }

    public List<ISerializableHook> getSerializableHooks() {
        List<ISerializableHook> serializableHooks = new ArrayList<>();
        for (HookType<?> type : getTypes()) {
            if (type.isSerializable()) {
                hooks.get(type).stream()
                    .filter(hook -> hook instanceof ISerializableHook)
                    .map(hook -> (ISerializableHook) hook)
                    .forEach(serializableHooks::add);
            }
        }
        return serializableHooks;
    }

    public static class Builder {
        private final Multimap<HookType<?>, IHook> hooks = HashMultimap.create();

        public <T extends IHook> Builder addHook(HookType<T> type, T hook) {
            hooks.put(type, hook);
            return this;
        }

        public Builder addHook(HookType<?> type, IHook... hookCollection) {
            for (IHook hook : hookCollection) {
                hooks.put(type, hook);
            }
            return this;
        }

        public HookMap build() {
            return new HookMap(hooks);
        }
    }
}
