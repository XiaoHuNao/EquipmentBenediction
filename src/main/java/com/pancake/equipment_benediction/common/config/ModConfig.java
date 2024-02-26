package com.pancake.equipment_benediction.common.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ModConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public final static ForgeConfigSpec.BooleanValue enableModifierHudDebug;

    static {
        BUILDER.comment("Config");

        enableModifierHudDebug = BUILDER.comment("Enable ModifierHud Debug")
                .define("enableModifierHudDebug", false);
    }
    static {
        SPEC = BUILDER.build();
    }
}
