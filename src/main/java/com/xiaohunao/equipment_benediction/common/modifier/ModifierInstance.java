package com.xiaohunao.equipment_benediction.common.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.xiaohunao.equipment_benediction.api.IModifier;
import com.xiaohunao.equipment_benediction.common.init.ModModifier;
import net.minecraft.resources.ResourceLocation;

public class ModifierInstance {
    public static final Codec<ModifierInstance> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.STRING.fieldOf("modifier").forGetter(ModifierInstance::getModifierRegistryName),
            Codec.INT.fieldOf("amplifier").forGetter(ModifierInstance::getAmplifier)
    ).apply(instance, ModifierInstance::new));

    private final IModifier modifier;
    private int amplifier;

    public ModifierInstance(IModifier modifier) {
        this(modifier, 0);
    }

    public ModifierInstance(IModifier modifier, int amplifier) {
        this.modifier = modifier;
        this.amplifier = amplifier;
    }

    public ModifierInstance(String modifier, int amplifier) {
        this(ModModifier.REGISTRY.get().getValue(ResourceLocation.tryParse(modifier)), amplifier);
    }

    public IModifier getModifier() {
        return modifier;
    }

    public String getModifierRegistryName() {
        return modifier.getRegistryName().toString();
    }

    public int getAmplifier() {
        return amplifier;
    }

    public void setAmplifier(int amplifier) {
        this.amplifier = amplifier;
    }
}
