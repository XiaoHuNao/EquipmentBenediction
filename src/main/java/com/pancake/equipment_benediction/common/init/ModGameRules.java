package com.pancake.equipment_benediction.common.init;

import net.minecraft.world.level.GameRules;


public class ModGameRules {
    public static GameRules.Key<GameRules.BooleanValue> DE_BUG_HUD;
    public static void registerRules() {
        DE_BUG_HUD = GameRules.register("ModifierHudDebug", GameRules.Category.PLAYER, GameRules.BooleanValue.create(false));
    }
}
