package com.xiaohunao.equipment_benediction.client.gui.widget;

import com.xiaohunao.equipment_benediction.EquipmentBenediction;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class LayoutControlButton extends Button {
    private static final ResourceLocation EXPAND = EquipmentBenediction.asResource("textures/gui/set_switcher/set_expand.png");
    private static final ResourceLocation EXPAND_HOVERED = EquipmentBenediction.asResource("textures/gui/set_switcher/set_expand_hovered.png");
    private static final ResourceLocation CONTRACT = EquipmentBenediction.asResource("textures/gui/set_switcher/set_contract.png");
    private static final ResourceLocation CONTRACT_HOVERED = EquipmentBenediction.asResource("textures/gui/set_switcher/set_contract_hovered.png");

    private static final int BUTTON_SIZE = 16;
    private int currentLayout = 0; // 0: 2x2, 1: 3x3, 2: 4x4

    public LayoutControlButton(int x, int y, OnPress onPress) {
        super(Button.builder(Component.empty(), onPress)
                .pos(x, y)
                .size(BUTTON_SIZE, BUTTON_SIZE));
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        ResourceLocation texture;
        if (currentLayout >= 2) { // 4x4, 需要收缩
            texture = this.isHovered() ? CONTRACT_HOVERED : CONTRACT;
        } else { // 2x2或3x3，需要扩展
            texture = this.isHovered() ? EXPAND_HOVERED : EXPAND;
        }

        guiGraphics.blit(texture, this.getX(), this.getY(), 0, 0, BUTTON_SIZE, BUTTON_SIZE, BUTTON_SIZE, BUTTON_SIZE);
    }

    public void cycleLayout() {
        currentLayout = (currentLayout + 1) % 3;
    }

    public int getCurrentLayout() {
        return currentLayout;
    }

    public int getGridSize() {
        return currentLayout + 2; // 2x2, 3x3, 4x4
    }
} 