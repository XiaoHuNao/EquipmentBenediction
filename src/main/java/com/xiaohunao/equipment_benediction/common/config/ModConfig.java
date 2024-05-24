package com.xiaohunao.equipment_benediction.common.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ModConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public final static ForgeConfigSpec.BooleanValue enableModifierHudDebug;

    public final static ForgeConfigSpec.BooleanValue enableEquipmentSetTooltip;

    static {
        BUILDER.comment("Config");

        enableModifierHudDebug = BUILDER.comment("Enable ModifierHud Debug")
                .define("enableModifierHudDebug", false);

        enableEquipmentSetTooltip = BUILDER.comment("Enable EquipmentSet Tooltip")
                .define("enableEquipmentSetTooltip", true);
    }
    static {
        SPEC = BUILDER.build();
    }
}
