package com.xiaohunao.equipment_benediction.client.gui.widget;

import com.xiaohunao.equipment_benediction.EquipmentBenediction;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ShowAllSetButton extends Button {
    public static final ResourceLocation SET_SHOW_ALL_CLOSE = EquipmentBenediction.asResource("textures/gui/set_switcher/set_show_all_close.png");
    public static final ResourceLocation SET_SHOW_ALL_OPEN = EquipmentBenediction.asResource("textures/gui/set_switcher/set_show_all_open.png");
    public static final ResourceLocation SET_SHOW_ALL_CLOSE_HOVERED = EquipmentBenediction.asResource("textures/gui/set_switcher/set_show_all_close_hovered.png");
    public static final ResourceLocation SET_SHOW_ALL_OPEN_HOVERED = EquipmentBenediction.asResource("textures/gui/set_switcher/set_show_all_open_hovered.png");
    public static final int BUTTON_SIZE = 16;
    private boolean showAllSet = false;

    public ShowAllSetButton(int x, int y, OnPress onPress) {
        super(Button.builder(Component.empty(), onPress)
                .pos(x, y)
                .size(BUTTON_SIZE, BUTTON_SIZE));
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        ResourceLocation texture;
        if (showAllSet) {
            texture = this.isHovered() ? SET_SHOW_ALL_OPEN_HOVERED : SET_SHOW_ALL_OPEN;
        } else {
            texture = this.isHovered() ? SET_SHOW_ALL_CLOSE_HOVERED : SET_SHOW_ALL_CLOSE;
        }
        guiGraphics.blit(texture, this.getX(), this.getY(), 0, 0, BUTTON_SIZE, BUTTON_SIZE, BUTTON_SIZE, BUTTON_SIZE);
    }

    public void setShowAllSet(boolean showAllSet) {
        this.showAllSet = showAllSet;
    }

    public boolean isShowAllSet() {
        return showAllSet;
    }
}
