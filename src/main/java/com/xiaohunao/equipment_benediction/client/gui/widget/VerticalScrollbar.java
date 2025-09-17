package com.xiaohunao.equipment_benediction.client.gui.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

/**
 * 简易纵向滚动条，不处理内容布局，只提供滚动值与绘制。
 */
public class VerticalScrollbar {

    private static final int DEFAULT_BACKGROUND_COLOR = 0xFF3A3A3A; // 默认背景颜色

    private int x;
    private int y;
    private int height;
    private int width;

    private double scroll; // 0..1
    private boolean dragging;

    private final int backgroundColor;
    private ResourceLocation scrollbarTexture;

    public VerticalScrollbar(int x, int y, int height, int width) {
        this.x = x;
        this.y = y;
        this.height = height;
        this.width = width;
        this.backgroundColor = DEFAULT_BACKGROUND_COLOR;
    }

    public VerticalScrollbar(int x, int y, int height, int width, ResourceLocation scrollbarTexture) {
        this.x = x;
        this.y = y;
        this.height = height;
        this.width = width;
        this.backgroundColor = DEFAULT_BACKGROUND_COLOR;
        this.scrollbarTexture = scrollbarTexture;
    }

    /**
     * 更新滚动条的轨道位置与尺寸，不影响当前 scroll 值。
     */
    public void setBounds(int x, int y, int height, int width) {
        this.x = x;
        this.y = y;
        this.height = height;
        this.width = width;
    }

    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int x0 = x;
        int y0 = y;
        int x1 = x + width;
        int y1 = y + height;
        guiGraphics.fill(x0, y0, x1, y1, backgroundColor);

        int knobHeight = Math.max(16, height / 6);
        int knobY = (int) (y + (height - knobHeight) * scroll);
        // 上下左右添加1像素内敛

        if (scrollbarTexture != null) {
            // 使用精灵图的上下两帧：上为未选中，下为拖动中
            int vOffset = dragging ? knobHeight : 0;
            guiGraphics.blit(scrollbarTexture, x0, knobY, 0, vOffset, width, knobHeight, width, knobHeight * 2);
        } else {
            guiGraphics.fill(x0 + 1, knobY + 1, x1 - 1, knobY + knobHeight - 1, 0xFFBEBEBE);
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) {
            return false;
        }
        if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
            dragging = true;
            updateFromMouse(mouseY);
            return true;
        }
        return false;
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0 && dragging) {
            dragging = false;
            return true;
        }
        return false;
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double dx, double dy) {
        if (dragging && button == 0) {
            updateFromMouse(mouseY);
            return true;
        }
        return false;
    }

    private void updateFromMouse(double mouseY) {
        double rel = (mouseY - y) / (double) Math.max(1, height);
        this.scroll = Math.max(0.0, Math.min(1.0, rel));
    }

    public void scrollByDelta(double delta, double contentHeight, double viewHeight) {
        double max = Math.max(1.0, contentHeight - viewHeight);
        double current = scroll * max;
        current = Math.max(0, Math.min(max, current + delta));
        this.scroll = max <= 0 ? 0 : current / max;
    }

    public double getScroll01() {
        return scroll;
    }

    public void setScroll01(double scroll01) {
        this.scroll = Math.max(0.0, Math.min(1.0, scroll01));
    }
}
