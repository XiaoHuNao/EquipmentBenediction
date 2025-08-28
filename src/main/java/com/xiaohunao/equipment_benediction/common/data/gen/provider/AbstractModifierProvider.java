package com.xiaohunao.equipment_benediction.common.data.gen.provider;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import com.xiaohunao.equipment_benediction.common.hook.dynamic.ISerializableHook;
import com.xiaohunao.equipment_benediction.common.init.register.EBDeferredHolder;
import com.xiaohunao.equipment_benediction.common.modifier.Modifier;
import com.xiaohunao.equipment_benediction.common.modifier.SerializableModifier;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public abstract class AbstractModifierProvider implements DataProvider {
    protected final PackOutput output;
    protected final Map<ResourceLocation, SerializableModifier> modifiers = new HashMap<>();

    protected AbstractModifierProvider(PackOutput output) {
        this.output = output;
    }

    protected abstract void addModifiers();

    protected void add(EBDeferredHolder<Modifier> holder, SerializableModifier modifier) {
        modifiers.put(holder.getId(), modifier);
    }

    protected SerializableModifier createModifier(ISerializableHook... hooks) {
        SerializableModifier.Builder builder = new SerializableModifier.Builder();
        for (ISerializableHook hook : hooks) {
            builder.addHook(hook);
        }
        return builder.build();
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        addModifiers();
        return CompletableFuture.allOf(modifiers.entrySet().stream()
            .map(entry -> {
                PackOutput.PathProvider pathProvider = output.createPathProvider(
                    PackOutput.Target.DATA_PACK, 
                    "modifiers"
                );
                return DataProvider.saveStable(
                    cache,
                    serializeModifier(entry.getValue()),
                    pathProvider.json(entry.getKey())
                );
            })
            .toArray(CompletableFuture[]::new)
        );
    }

    private JsonElement serializeModifier(SerializableModifier modifier) {
        return SerializableModifier.CODEC.encodeStart(JsonOps.INSTANCE, modifier)
            .getOrThrow();
    }

    @Override
    public String getName() {
        return "Equipment Benediction Modifiers";
    }
}