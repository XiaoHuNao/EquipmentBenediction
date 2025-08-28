package com.xiaohunao.equipment_benediction.common.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.xiaohunao.equipment_benediction.common.hook.HookMap;
import com.xiaohunao.equipment_benediction.common.hook.HookType;
import com.xiaohunao.equipment_benediction.common.hook.dynamic.ISerializableHook;

public class SerializableModifier extends Modifier {
    public static final Codec<SerializableModifier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.list(ISerializableHook.CODEC).fieldOf("hooks").forGetter(modifier -> {
            HookMap hookMap = modifier.getHookMap();
            return hookMap.getSerializableHooks();
        })
    ).apply(instance, hooks -> {
        Builder builder = new Builder();
        hooks.forEach(builder::addHook);
        return builder.build();
    }));

    private SerializableModifier(HookMap hookMap) {
        super(hookMap);
    }

    public static class Builder {
        private final HookMap.Builder hooks = HookMap.builder();

        public Builder addHook(ISerializableHook hook) {
            HookType<?> hookType = ISerializableHook.getHookType(hook.getClass());
            if (hookType != null) {
                hooks.addHook(hookType, hook);
            }
            return this;
        }

        public Builder addHooks(ISerializableHook... hooks) {
            for (ISerializableHook hook : hooks) {
                addHook(hook);
            }
            return this;
        }

        public SerializableModifier build() {
            return new SerializableModifier(hooks.build());
        }
    }
}
