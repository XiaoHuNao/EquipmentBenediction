package com.xiaohunao.equipment_benediction.client.gui.widget;

import com.xiaohunao.equipment_benediction.EquipmentBenediction;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class LayoutExpandButton extends Button {
    private static final ResourceLocation EXPAND = EquipmentBenediction.asResource("textures/gui/set_switcher/set_expand.png");
    private static final ResourceLocation EXPAND_HOVERED = EquipmentBenediction.asResource("textures/gui/set_switcher/set_expand_hovered.png");
    private static final ResourceLocation CONTRACT = EquipmentBenediction.asResource("textures/gui/set_switcher/set_contract.png");
    private static final ResourceLocation CONTRACT_HOVERED = EquipmentBenediction.asResource("textures/gui/set_switcher/set_contract_hovered.png");

    public static final int BUTTON_SIZE = 16;
    private boolean isGridLayout = false;

    public LayoutExpandButton(int x, int y, OnPress onPress) {
        super(Button.builder(Component.empty(), onPress)
                .pos(x, y)
                .size(BUTTON_SIZE, BUTTON_SIZE));
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        ResourceLocation texture;
        if (isGridLayout) {
            texture = this.isHovered() ? CONTRACT_HOVERED : CONTRACT;
        } else {
            texture = this.isHovered() ? EXPAND_HOVERED : EXPAND;
        }
        guiGraphics.blit(texture, this.getX(), this.getY(), 0, 0, BUTTON_SIZE, BUTTON_SIZE, BUTTON_SIZE, BUTTON_SIZE);
    }

    public void setGridLayout(boolean gridLayout) {
        this.isGridLayout = gridLayout;
    }

    public boolean isGridLayout() {
        return isGridLayout;
    }
}
