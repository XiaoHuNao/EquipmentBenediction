package com.xiaohunao.equipment_benediction.client.tooltip;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.xiaohunao.equipment_benediction.api.IEquipmentSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;


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
    public void renderImage(Font font, int tooltipX, int tooltipY, PoseStack poseStack, ItemRenderer itemRenderer, int p_194053_) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        long dayTime = level.getDayTime();

        poseStack.pushPose();
        poseStack.translate(tooltipX, tooltipY, 0);
        poseStack.scale(0.5f, 0.5f, 1.0f);

        Object[] array = set.getGroup().getEquipages().values().toArray();

        for (int i = 0; i < array.length; i++) {
            Ingredient ingredient = (Ingredient) array[i];
            ItemStack[] items = ingredient.getItems();

            if (items.length > 1) {
                int itemIndexToShow = (int) ((dayTime / 20) % items.length);
                ItemStack itemToShow = items[itemIndexToShow];
                itemRenderer.renderGuiItem(itemToShow, i * 18, 0);
            } else if (items.length == 1) {
                itemRenderer.renderGuiItem(items[0], i * 18, 0);
            }
        }

        poseStack.popPose();
        RenderSystem.applyModelViewMatrix();
    }

}
