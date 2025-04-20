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
            // 绘制半透明背景
            guiGraphics.fillGradient(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, 
                0x30FFFFFF, 0x30FFFFFF);
        }
        
        // 绘制文字
        int textWidth = Minecraft.getInstance().font.width(this.getMessage());
        int textX = this.getX() + (this.width - textWidth) / 2;
        guiGraphics.drawString(Minecraft.getInstance().font, this.getMessage(), textX, this.getY(), 0x808080);

        // 绘制展开/折叠指示器
        String indicator = expanded ? "▼" : "▶";
        guiGraphics.drawString(Minecraft.getInstance().font, indicator, this.getX() + 2, this.getY(), 0x808080);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.isMouseOver(mouseX, mouseY)) {
            this.onPress();
            return true;
        }
        return false;
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