package com.xiaohunao.equipment_benediction.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.xiaohunao.equipment_benediction.EquipmentBenediction;
import com.xiaohunao.equipment_benediction.api.EquipmentSetManager;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSet;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquippableSetData;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.Minecraft;

public class EquippableSetButton extends Button {
    private static final EquipmentSetManager setManager = EquipmentSetManager.getInstance();
    private static final ResourceLocation texture = EquipmentBenediction.asResource("textures/gui/set_switcher/set_button.png");
    private static final ResourceLocation textureHovered = EquipmentBenediction.asResource("textures/gui/set_switcher/set_button_hovered.png");
    private static final ResourceLocation textureSelected = EquipmentBenediction.asResource("textures/gui/set_switcher/set_button_selected.png");

    private final EquippableSetData setData;
    private final EquipmentSet equipmentSet;
    private final boolean exclusive;
    private boolean selected;
    private float scrollOffset = 0;
    private final int maxTextWidth = 80; // 文本最大显示宽度
    private long lastScrollTime = 0;
    private final int scrollDelay = 50; // 滚动延迟(ms)
    private final int scrollSpeed = 1; // 每次滚动的像素数

    public EquippableSetButton(int x, int y, int width, int height, 
                              EquippableSetData setData, EquipmentSet equipmentSet, 
                              boolean exclusive, OnPress onPress) {
        super(x, y, width, height, Component.empty(), onPress, DEFAULT_NARRATION);
        this.setData = setData;
        this.equipmentSet = equipmentSet;
        this.exclusive = exclusive;
        this.selected = false;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        ResourceLocation currentTexture = this.selected ? textureSelected :
                (this.isHovered ? textureHovered : texture);
        
        // 保存当前渲染状态
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        
        // 渲染按钮
        guiGraphics.blit(currentTexture, this.getX(), this.getY(), 0, 0, this.width, this.height, this.width, this.height);
        
        // 获取套装在组内的索引
        int setIndex = 0;
        for (EquippableSetData setData : equipmentSet.getEquippableGroup().getEquippableSets()) {
            if (setData == this.setData) {
                break;
            }
            setIndex++;
        }

        // 渲染套装效果描述（带滚动）
        ResourceLocation resource = setManager.getResource(equipmentSet);
        String translationKey = resource.getNamespace() + "." + resource.getPath();
        Component description = Component.translatable(
            "equipment_benediction.set_switcher." + translationKey + ".data." + setIndex
        );
        
        // 设置缩放比例
        float scale = 0.8f; // 缩放到80%大小
        
        // 计算缩放后的文本宽度和高度
        int textWidth = (int)(Minecraft.getInstance().font.width(description) * scale);
        int fontHeight = (int)(Minecraft.getInstance().font.lineHeight * scale);
        
        // 计算文本垂直居中位置，考虑缩放
        int textY = this.getY() + (this.height - fontHeight) / 2;
        
        // 保存当前矩阵状态
        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();
        
        // 应用缩放
        poseStack.translate(this.getX() + 4, textY, 0);
        poseStack.scale(scale, scale, 1.0f);
        poseStack.translate(-(this.getX() + 4), -textY, 0);
        
        if (textWidth > maxTextWidth) {
            // 需要滚动显示
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastScrollTime > scrollDelay) {
                scrollOffset += scrollSpeed;
                if (scrollOffset > textWidth) {
                    scrollOffset = -maxTextWidth; // 重置到开始位置
                }
                lastScrollTime = currentTime;
            }
            
            // 启用剪裁，注意剪裁区域不需要缩放
            guiGraphics.enableScissor(
                this.getX() + 4,
                this.getY(),
                this.getX() + maxTextWidth,
                this.getY() + this.height
            );
            
            // 渲染滚动文本
            guiGraphics.drawString(Minecraft.getInstance().font, description,
                this.getX() + 4 - (int)(scrollOffset / scale),
                textY,
                0xFFFFFF);
            
            guiGraphics.disableScissor();
        } else {
            // 不需要滚动，直接渲染
            guiGraphics.drawString(Minecraft.getInstance().font, description,
                this.getX() + 4,
                textY,
                0xFFFFFF);
        }
        
        // 恢复矩阵状态
        poseStack.popPose();
        
        // 恢复渲染状态
        RenderSystem.disableBlend();

        // 如果是独占的，添加一个星号标记
        if (exclusive) {
            guiGraphics.drawString(Minecraft.getInstance().font, "*", 
                                 this.getX() + this.width - 8, this.getY() + (this.height - 8) / 2, 0xFFFF00);
        }
    }

    public EquippableSetData getSetData() {
        return setData;
    }

    public EquipmentSet getEquipmentSet() {
        return equipmentSet;
    }

    public boolean isExclusive() {
        return exclusive;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
} 