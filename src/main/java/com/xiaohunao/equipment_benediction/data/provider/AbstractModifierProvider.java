package com.xiaohunao.equipment_benediction.data.provider;

import com.xiaohunao.equipment_benediction.common.init.register.EBDeferredHolder;
import com.xiaohunao.equipment_benediction.common.init.register.EBDynamicHolder;
import com.xiaohunao.equipment_benediction.common.modifier.Modifier;
import com.xiaohunao.equipment_benediction.common.modifier.SerializableModifier;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.JsonCodecProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public abstract class AbstractModifierProvider {
    protected final PackOutput output;
    protected final Map<ResourceLocation, SerializableModifier> modifiers = new HashMap<>();

    protected AbstractModifierProvider(PackOutput output) {
        this.output = output;
    }

    /**
     * 添加动态修饰器
     * @param holder 动态修饰器持有者
     * @param modifier 修饰器实例
     */
    protected void addDynamicModifier(EBDynamicHolder<Modifier> holder, SerializableModifier modifier) {
        modifiers.put(holder.getId(), modifier);
    }

    /**
     * 添加静态修饰器
     * @param holder 静态修饰器持有者
     * @param modifier 修饰器实例
     */
    protected void addStaticModifier(EBDeferredHolder<Modifier> holder, Modifier modifier) {
        if (!(modifier instanceof SerializableModifier serializableModifier)) {
            throw new IllegalArgumentException("Static modifier must be serializable: " + holder.getId());
        }
        modifiers.put(holder.getId(), serializableModifier);
    }

    protected abstract void buildModifiers(BiConsumer<ResourceLocation, SerializableModifier> consumer);
} 