package com.xiaohunao.equipment_benediction.common.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.xiaohunao.equipment_benediction.api.manager.ModifierManager;
import net.minecraft.resources.ResourceLocation;

public class ModifierInstance {
    public static final Codec<ModifierInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(ModifierInstance::getId),
            Codec.INT.fieldOf("level").forGetter(ModifierInstance::getLevel)
    ).apply(instance, ModifierInstance::new));
    private final ResourceLocation id;
    private final int level;

    public ModifierInstance(ResourceLocation id, int level) {
        this.id = id;
        this.level = level;
    }

    public ResourceLocation getId() {
        return id;
    }

    public int getLevel() {
        return level;
    }

    public Modifier getModifier() {
        ModifierManager instance = ModifierManager.getInstance();
        return instance.getResource(id);
    }
}
