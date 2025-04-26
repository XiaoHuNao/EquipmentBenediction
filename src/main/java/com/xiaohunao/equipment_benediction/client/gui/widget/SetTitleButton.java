package com.xiaohunao.equipment_benediction.client.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class SetTitleButton extends TransparentButton {
    private boolean expanded = false;

    public SetTitleButton(int x, int y, int width, int height, Component message, OnPress onPress) {
        super(x, y, width, height, message, onPress);
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (this.isHovered()) {
            guiGraphics.fillGradient(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, 
                0x30FFFFFF, 0x30FFFFFF);
        }

        String indicator = expanded ? "▼" : "▶";
        guiGraphics.drawString(Minecraft.getInstance().font, indicator, this.getX() + 2, this.getY(), 0x808080);

        int textWidth = Minecraft.getInstance().font.width(this.getMessage());
        int textX = this.getX() + 12 + (this.width - 12 - textWidth) / 2; // 12是为了给指示器留出空间
        guiGraphics.drawString(Minecraft.getInstance().font, this.getMessage(), textX, this.getY(), 0x808080);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.isMouseOver(mouseX, mouseY)) {
            this.playDownSound(Minecraft.getInstance().getSoundManager());
            this.onPress.onPress(this);
            return true;
        }
        return false;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        boolean result = mouseX >= this.getX() && mouseY >= this.getY() &&
                        mouseX < this.getX() + this.width && mouseY < this.getY() + this.height;
        return result;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public void toggleExpanded() {
        this.expanded = !this.expanded;
    }
} 