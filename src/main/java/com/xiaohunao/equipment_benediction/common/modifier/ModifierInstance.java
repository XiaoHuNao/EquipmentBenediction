package com.xiaohunao.equipment_benediction.common.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.xiaohunao.equipment_benediction.common.init.ModModifier;
import dev.latvian.mods.rhino.Context;
import net.minecraft.resources.ResourceLocation;

public class ModifierInstance {
    public static final Codec<ModifierInstance> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.STRING.fieldOf("modifier").forGetter(ModifierInstance::getModifierRegistryName),
            Codec.INT.fieldOf("amplifier").forGetter(ModifierInstance::getAmplifier)
    ).apply(instance, ModifierInstance::new));

    private Modifier modifier;
    private ResourceLocation modifierRegistryName;
    private int amplifier;

    public ModifierInstance(Modifier modifier) {
        this(modifier, 0);
    }

    public ModifierInstance(Modifier modifier, int amplifier) {
        this.modifier = modifier;
        this.amplifier = amplifier;
    }

    public ModifierInstance(String modifier, int amplifier) {
        this.modifierRegistryName = ResourceLocation.tryParse(modifier);
        this.amplifier = amplifier;
    }

    public ModifierInstance(String modifier) {
        this(modifier, 0);
    }

    public Modifier getModifier() {
        return modifier != null ? modifier : ModModifier.MODIFIER_MAP.get(modifierRegistryName);
    }

    public String getModifierRegistryName() {
        return getModifier().getRegistryName().toString();
    }

    public int getAmplifier() {
        return amplifier;
    }

    public void setAmplifier(int amplifier) {
        this.amplifier = amplifier;
    }
}
