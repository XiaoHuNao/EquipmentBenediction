package com.pancake.equipment_benediction.common.modifier;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.pancake.equipment_benediction.api.IModifier;
import com.pancake.equipment_benediction.common.init.ModModifiers;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.*;

public abstract class Modifier implements IModifier {
    protected ModifierHandler handler;
    protected int textColor = 0xe74c3c;
    private String translationKey;
    private Component description;
    private Component displayName;


    public Modifier() {
        ModifierHandler.Builder handle = new ModifierHandler.Builder();
        init(handle);
        this.handler = handle.build();
    }

    public abstract void init(ModifierHandler.Builder handle);
    @Override
    public ModifierHandler getHandler() {
        return handler;
    }


    @Override
    public void registryEvent() {
        getHandler().getEventConsumers().forEach(info -> {
            MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, info.eventClass(), info.consumer());
        });
    }

    @Override
    public void unregisterEvent() {
        getHandler().getEventConsumers().forEach(info -> {
            MinecraftForge.EVENT_BUS.unregister(info.consumer());
        });
    }

    @Override
    public void addEffectModifier(Player player) {
        getHandler().getEffectModifiers().forEach((effect, modifier) -> {
            player.addEffect(modifier);
        });
    }

    @Override
    public void addAttributeModifier(Player player) {
        getHandler().getAttributeModifiers().forEach((attribute, modifier) -> {
            AttributeInstance attributeInstance = player.getAttribute(attribute);
            if (attributeInstance != null){
                attributeInstance.addTransientModifier(modifier);
            }
        });
    }

    @Override
    public void removeAttributeModifier(Player player) {
        getHandler().getAttributeModifiers().forEach((attribute, modifier) -> {
            AttributeInstance attributeInstance = player.getAttribute(attribute);
            if (attributeInstance != null){
                attributeInstance.removeModifier(modifier);
            }
        });
    }

    @Override
    public void removeEffectModifier(Player player) {
        getHandler().getEffectModifiers().forEach((effect, modifier) -> {
            player.removeEffect(effect);
        });
    }

    @Override
    public void applyDisplayName(List<Component> components) {
        Component displayName = getDisplayName();
        if (displayName != null) {
            components.add(displayName);
        }
    }

    @Override
    public Component getDisplayName() {
        if (displayName == null) {
            displayName = Component.translatable(getTranslationKey()).withStyle(style -> style.withColor(getTextColor()));
        }
        return displayName;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    @Override
    public final TextColor getTextColor() {
        return TextColor.fromRgb(textColor);
    }
    @Override
    public final String getTranslationKey() {
        if (this.translationKey == null) {
            this.translationKey = Util.makeDescriptionId("modifier", getRegistryName());
        }

        return this.translationKey;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj != null && this.getClass() == obj.getClass()) {
            Modifier modifier = (Modifier)obj;
            return Objects.equals(this.getRegistryName(), modifier.getRegistryName());
        } else {
            return false;
        }
    }
    @Override
    public ResourceLocation getRegistryName() {
        return ModModifiers.MODIFIER_REGISTRY.get().getKey(this);
    }

    @Override
    public Codec<? extends IModifier> codec() {
        return Codec.unit(() ->ModModifiers.MODIFIER_REGISTRY.get().getValue(getRegistryName()));
    }

    @Override
    public IModifier type() {
        return ModModifiers.MODIFIER_REGISTRY.get().getValue(getRegistryName());
    }
}
