package com.xiaohunao.equipment_benediction.client.gui.hud;

import com.mojang.blaze3d.vertex.PoseStack;
import com.xiaohunao.equipment_benediction.EquipmentBenediction;
import com.xiaohunao.equipment_benediction.api.IModifier;
import com.xiaohunao.equipment_benediction.common.config.ModConfig;
import com.xiaohunao.equipment_benediction.common.modifier.ModifierHelper;
import com.xiaohunao.equipment_benediction.common.modifier.ModifierInstance;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;


public class ModifierDeBugHudRenderer {
    static Minecraft minecraft = Minecraft.getInstance();

    public static IGuiOverlay MODIFIER_DEBUG_HUD = (forgeGui, poseStack, partialTicks, screenWidth, screenHeight) -> {
        render(forgeGui, poseStack, screenWidth, screenHeight, minecraft.player);
    };

    public static void render(ForgeGui forgeGui, PoseStack poseStack, int width, int height, LocalPlayer player) {
        if (!ModConfig.enableModifierHudDebug.get()) {
            return;
        }

        ListTag modifierTags = ModifierHelper.getPlayerListTag(player);
        modifierTags.forEach((tag) -> {
            ModifierInstance.CODEC.parse(NbtOps.INSTANCE, tag)
                    .resultOrPartial(EquipmentBenediction.LOGGER::error)
                    .ifPresent((instance) -> {
                        IModifier modifier = instance.getModifier();
                        int amplifier = instance.getAmplifier();
                        GuiComponent.drawString(poseStack, minecraft.font, modifier.getDisplayName(), 0, 0, 0xFFFFFF);
                        GuiComponent.drawString(poseStack, minecraft.font, "Amplifier: " + amplifier, 200, 0, 0xFFFFFF);
                    });
        });
    }

    public static void registerOverlay(RegisterGuiOverlaysEvent event) {
        event.registerAbove(VanillaGuiOverlay.FOOD_LEVEL.id(), "modifier_debug", MODIFIER_DEBUG_HUD);
    }
}
