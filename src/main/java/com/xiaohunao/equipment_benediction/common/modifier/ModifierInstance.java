package com.xiaohunao.equipment_benediction.common.modifier;

import net.minecraft.resources.ResourceLocation;

public class ModifierInstance {
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
}
