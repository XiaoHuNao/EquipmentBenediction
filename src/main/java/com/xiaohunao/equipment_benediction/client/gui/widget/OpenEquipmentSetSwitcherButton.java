package com.xiaohunao.equipment_benediction.client.gui.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class OpenEquipmentSetSwitcherButton extends Button {
    private long pressStartTime = 0;
    private static final int PRESS_TIME_REQUIRED = 1000; // 3秒 = 3000毫秒
    private boolean isPressing = false;

    public OpenEquipmentSetSwitcherButton(int x, int y, int width, int height, Component message, OnPress onPress) {
        super(Button.builder(message, onPress)
                .pos(x, y)
                .size(width, height));
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (this.isHovered()) {
            // 绘制半透明背景
            guiGraphics.fillGradient(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, 
                0x30FFFFFF, 0x30FFFFFF);
            
            // 如果正在按住左键
            if (isPressing) {
                long pressTime = System.currentTimeMillis() - pressStartTime;
                
                // 如果达到3秒
                if (pressTime >= PRESS_TIME_REQUIRED) {
                    // 触发按钮点击
                    this.onPress();
                    // 重置状态
                    isPressing = false;
                } else {
                    // 显示进度条
                    float progress = (float) pressTime / PRESS_TIME_REQUIRED;
                    // 绘制进度条
                    int progressWidth = (int) (this.width * progress);
                    guiGraphics.fillGradient(this.getX(), this.getY() + this.height - 2, 
                        this.getX() + progressWidth, this.getY() + this.height, 
                        0xFFFFFFFF, 0xFFFFFFFF);
                }
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.isHovered() && button == 0) { // 0 表示左键
            pressStartTime = System.currentTimeMillis();
            isPressing = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) { // 0 表示左键
            isPressing = false;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }
} 