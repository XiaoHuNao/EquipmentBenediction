package com.xiaohunao.equipment_benediction.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.xiaohunao.equipment_benediction.EquipmentBenediction;
import com.xiaohunao.equipment_benediction.api.manager.EquipmentSetManager;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSet;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSetBranch;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

/**
 * 装备集按钮组件
 * 用于显示和选择装备集
 */
public class EquippableSetButton extends Button {
    // 静态资源
    private static final EquipmentSetManager SET_MANAGER = EquipmentSetManager.getInstance();
    private static final ResourceLocation TEXTURE_NORMAL = EquipmentBenediction.asResource("textures/gui/set_switcher/set_button.png");
    private static final ResourceLocation TEXTURE_HOVERED = EquipmentBenediction.asResource("textures/gui/set_switcher/set_button_hovered.png");
    private static final ResourceLocation TEXTURE_SELECTED = EquipmentBenediction.asResource("textures/gui/set_switcher/set_button_selected.png");
    
    // 常量
    private static final String STAR_SYMBOL = "*";
    private static final String CROSS_SYMBOL = "✕";
    private static final int STAR_COLOR = 0xFFFF00;  // 黄色
    private static final int TEXT_COLOR = 0xFFFFFF;  // 白色
    private static final int CROSS_COLOR = 0xFF0000; // 红色
    private static final int TEXT_PADDING = 4;       // 文本内边距
    private static final int STAR_SPACING = 2;       // 星号后的间距
    private static final float TEXT_SCALE = 0.8f;    // 文本缩放比例
    
    // 滚动相关
    private static final int SCROLL_DELAY = 50;      // 滚动延迟(毫秒)
    private static final int SCROLL_SPEED = 1;       // 每次滚动的像素数
    private final int maxTextWidth = 80;             // 文本最大显示宽度
    private float scrollOffset = 0;                  // 当前滚动偏移量
    private long lastScrollTime = 0;                 // 上次滚动时间
    
    // 按钮数据
    private final EquipmentSetBranch setData;
    private final EquipmentSet equipmentSet;
    private final boolean exclusive;
    private boolean selected;

    /**
     * 创建一个装备集按钮
     */
    public EquippableSetButton(int x, int y, int width, int height,
                               EquipmentSetBranch setData, EquipmentSet equipmentSet,
                               boolean exclusive, OnPress onPress) {
        super(x, y, width, height, Component.empty(), onPress, DEFAULT_NARRATION);
        this.setData = setData;
        this.equipmentSet = equipmentSet;
        this.exclusive = exclusive;
        this.selected = false;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        ResourceLocation currentTexture = this.selected ? TEXTURE_SELECTED :
                (this.isHovered ? TEXTURE_HOVERED : TEXTURE_NORMAL);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        guiGraphics.blit(currentTexture, this.getX(), this.getY(), 0, 0, 
                        this.width, this.height, this.width, this.height);

        var font = Minecraft.getInstance().font;
        int fontHeight = font.lineHeight;
        int centerY = this.getY() + (this.height - fontHeight) / 2 + 1;
        

        renderButtonContent(guiGraphics, font, centerY);

        RenderSystem.disableBlend();
    }

    private void renderButtonContent(GuiGraphics guiGraphics, net.minecraft.client.gui.Font font, int centerY) {
        int setIndex = getSetIndex();

        int textStartX = this.getX() + TEXT_PADDING;

        if (exclusive) {
            int starWidth = font.width(STAR_SYMBOL);
            guiGraphics.drawString(font, STAR_SYMBOL, textStartX, centerY, STAR_COLOR);
            textStartX += starWidth + STAR_SPACING;
        }

        renderScaledText(guiGraphics, font, getDescriptionText(setIndex), textStartX, centerY);

        if (!isValidForPlayer(Minecraft.getInstance().player)) {
            int crossWidth = font.width(CROSS_SYMBOL);
            guiGraphics.drawString(font, CROSS_SYMBOL, 
                                 this.getX() + this.width - crossWidth - TEXT_PADDING, 
                                 centerY, CROSS_COLOR);
        }
    }
    

    private int getSetIndex() {
        int index = 0;
        for (EquipmentSetBranch data : equipmentSet.getEquippableGroup().equippableMaps().values()) {
            if (data == this.setData) {
                break;
            }
            index++;
        }
        return index;
    }
    

    private Component getDescriptionText(int setIndex) {
        ResourceLocation resource = SET_MANAGER.getResource(equipmentSet);
        String translationKey = resource.getNamespace() + "." + resource.getPath();
        return Component.translatable(
            "equipment_benediction.set_switcher." + translationKey + ".data." + setIndex
        );
    }
    

    private void renderScaledText(GuiGraphics guiGraphics, Font font,
                                Component description, int textStartX, int textY) {
        int textWidth = (int)(font.width(description) * TEXT_SCALE);
        int scaledFontHeight = (int)(font.lineHeight * TEXT_SCALE);

        int scaledTextY = this.getY() + (this.height - scaledFontHeight) / 2;

        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();

        poseStack.translate(textStartX, scaledTextY, 0);
        poseStack.scale(TEXT_SCALE, TEXT_SCALE, 1.0f);
        poseStack.translate(-textStartX, -scaledTextY, 0);

        int crossWidth = font.width(CROSS_SYMBOL);

        int starWidth = exclusive ? (font.width(STAR_SYMBOL) + STAR_SPACING) : 0;
        int availableWidth = maxTextWidth - starWidth - (crossWidth + TEXT_PADDING);

        if (textWidth > availableWidth) {
            renderScrollingText(guiGraphics, font, description, textStartX, 
                              scaledTextY, availableWidth, textWidth);
        } else {
            guiGraphics.drawString(font, description, textStartX, scaledTextY, TEXT_COLOR);
        }

        poseStack.popPose();
    }

    private void renderScrollingText(GuiGraphics guiGraphics, Font font,
                                   Component description, int textStartX, int textY, 
                                   int availableWidth, int textWidth) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastScrollTime > SCROLL_DELAY) {
            scrollOffset += SCROLL_SPEED;
            if (scrollOffset > textWidth) {
                scrollOffset = -availableWidth; // 重置到开始位置
            }
            lastScrollTime = currentTime;
        }

        guiGraphics.enableScissor(
            textStartX,
            this.getY(),
            textStartX + availableWidth,
            this.getY() + this.height
        );

        guiGraphics.drawString(font, description,
            textStartX - (int)(scrollOffset / TEXT_SCALE),
            textY,
            TEXT_COLOR);
        
        guiGraphics.disableScissor();
    }

    public EquipmentSetBranch getSetData() {
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

    public boolean isValidForPlayer(Player player) {
        return setData.isValid(player);
    }
} 