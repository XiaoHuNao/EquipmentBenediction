package com.xiaohunao.equipment_benediction.client.tooltip;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.xiaohunao.equipment_benediction.api.IEquipmentSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;


public record EquipmentSetTooltipComponent(int width, int height, IEquipmentSet set) implements ClientTooltipComponent, TooltipComponent {
    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getWidth(Font font) {
        return width;
    }

    @Override
    public void renderImage(@NotNull Font font, int tooltipX, int tooltipY, @NotNull GuiGraphics guiGraphics) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        long dayTime = level.getDayTime();

        PoseStack pose = guiGraphics.pose();
        pose.pushPose();
        pose.translate(tooltipX, tooltipY, 0);
        pose.scale(0.5f, 0.5f, 1.0f);

        Object[] array = set.getGroup().getEquipages().values().toArray();

        for (int i = 0; i < array.length; i++) {
            Ingredient ingredient = (Ingredient) array[i];
            ItemStack[] items = ingredient.getItems();

            if (items.length > 1) {
                int itemIndexToShow = (int) ((dayTime / 20) % items.length);
                ItemStack itemToShow = items[itemIndexToShow];
                guiGraphics.renderItem(itemToShow, i * 18, 0);
            } else if (items.length == 1) {
                guiGraphics.renderItem(items[0], i * 18, 0);
            }
        }

        pose.popPose();
        RenderSystem.applyModelViewMatrix();
    }
}
