package com.xiaohunao.equipment_benediction.client.gui.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.xiaohunao.equipment_benediction.EquipmentBenediction;
import com.xiaohunao.equipment_benediction.api.manager.EquipmentSetManager;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSet;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSetBranch;
import com.xiaohunao.equipment_benediction.common.equippable.IWearable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

public class BranchCardWidget extends AbstractWidget {

    public static final ResourceLocation BACKGROUND_TEXTURE = EquipmentBenediction.asResource("textures/gui/baranch_card.png");
    public static final ResourceLocation CONFIRM_TEXTURE = EquipmentBenediction.asResource("textures/gui/confirm_button.png");
    public static final ResourceLocation SLOT_EXPAND = EquipmentBenediction.asResource("textures/gui/slot_expand.png");

    public static final int TEXTURE_WIDTH = 100;
    public static final int TEXTURE_HEIGHT = 30;
    public static final int CARD_HEIGHT = 15;
    public static final int HOVER_OFFSET = CARD_HEIGHT;
    public static final int WEARABLE_ICON_SIZE = 9;
    public static final int WEARABLE_CARD_WIDTH = WEARABLE_ICON_SIZE + 2;
    public static final int TOOLTIP_WIDTH = 1;
    public static final int END_HEIGHT = 1;
    public static final int CARD_SEPARATOR = 1;
    public static final int SLOT_EXPAND_BUTTON_SIZE = 7;

    private static final int TEXT_LEFT_PADDING = 4;
    private static final int TEXT_TOP_PADDING = 3;
    private static final int CONFIRM_BUTTON_SIZE = 9;
    private static final int CARD_INNER_TEXT_WIDTH = 80;
    private static final int MIN_TOOLTIP_AREA = 12;
    private static final int ICON_SPACING = 2;
    private static final int BUTTON_MARGIN = 2;
    private static final int WEARABLE_RENDER_MAX_COUNT = 8;


    private EquipmentSet equipmentSet;
    private int contentHeight;
    private int viewportX;
    private int viewportY;
    private int viewportHeight;
    private double scroll01; // 0..1

    // 每个分支对应一个确认按钮
    private final Map<EquipmentSetBranch, SpriteIconButton> branchToConfirmButton = new LinkedHashMap<>();
    // 每个分支对应一个展开按钮
    private final Map<EquipmentSetBranch, SpriteIconButton> branchToExpandButton = new LinkedHashMap<>();

    /**
     * 构造函数
     *
     * @param x 初始X坐标
     * @param y 初始Y坐标
     */
    public BranchCardWidget(int x, int y) {
        super(x, y, 0, 0, Component.empty());
    }

    /**
     * 设置视口区域
     *
     * @param x 视口X坐标
     * @param y 视口Y坐标
     * @param width 视口宽度
     * @param height 视口高度
     */
    public void setViewport(int x, int y, int width, int height) {
        this.viewportX = x;
        this.viewportY = y;
        this.viewportHeight = height;
        this.setX(x);
        this.setY(y);
        this.width = width;
        this.height = height;
    }

    /**
     * 设置装备套装
     *
     * @param equipmentSet 要显示的装备套装
     */
    public void setEquipmentSet(EquipmentSet equipmentSet) {
        // 若传入与当前相同且按钮已存在，则不重复重建，避免选中状态被重置
        if (this.equipmentSet == equipmentSet && !this.branchToConfirmButton.isEmpty()) {
            return;
        }

        // 先记录旧按钮的选中状态，用于尽量恢复
        Map<EquipmentSetBranch, Boolean> previousSelection = new HashMap<>();
        for (Map.Entry<EquipmentSetBranch, SpriteIconButton> e : this.branchToConfirmButton.entrySet()) {
            previousSelection.put(e.getKey(), e.getValue().isSelected());
        }

        this.equipmentSet = equipmentSet;
        this.branchToConfirmButton.clear();
        this.branchToExpandButton.clear();

        if (equipmentSet != null) {
            List<EquipmentSetBranch> branches = new ArrayList<>(equipmentSet.allBranch());
            for (EquipmentSetBranch branch : branches) {
                SpriteIconButton btn = new SpriteIconButton.Builder(button -> {
                    // 按钮点击处理逻辑（占位，按需实现）
                })
                        .bounds(0, 0, CONFIRM_BUTTON_SIZE, CONFIRM_BUTTON_SIZE)
                        .sprite(CONFIRM_TEXTURE)
                        .enableClickAnimation(true)
                        .stateFrame(SpriteIconButton.ButtonVisualState.NORMAL, 0)
                        .stateFrame(SpriteIconButton.ButtonVisualState.SELECTED, 1)
                        .build();
                Boolean wasSelected = previousSelection.get(branch);
                if (wasSelected != null && wasSelected) {
                    btn.setSelected(true);
                }
                branchToConfirmButton.put(branch, btn);

                // 构建展开按钮
                SpriteIconButton expandBtn = new SpriteIconButton.Builder(button -> {
                    // 展开按钮点击处理逻辑（占位，按需实现）
                })
                        .bounds(0, 0, SLOT_EXPAND_BUTTON_SIZE, SLOT_EXPAND_BUTTON_SIZE)
                        .sprite(SLOT_EXPAND)
                        .enableClickAnimation(true)
                        .stateFrame(SpriteIconButton.ButtonVisualState.NORMAL, 0)
                        .stateFrame(SpriteIconButton.ButtonVisualState.SELECTED, 1)
                        .build();
                branchToExpandButton.put(branch, expandBtn);
            }
        }
    }

    /**
     * 设置滚动位置
     *
     * @param scroll01 滚动位置，范围0.0-1.0
     */
    public void setScroll01(double scroll01) {
        this.scroll01 = Math.max(0.0, Math.min(1.0, scroll01));
    }

    /**
     * 获取内容总高度
     *
     * @return 内容总高度
     */
    public int getContentHeight() {
        return contentHeight;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (equipmentSet == null) {
            contentHeight = 0;
            return;
        }

        Font font = Minecraft.getInstance().font;
        List<EquipmentSetBranch> branches = new ArrayList<>(equipmentSet.allBranch());

        // 计算总内容高度
        contentHeight = calculateTotalContentHeight(font, branches);
        int yOffset = (int) ((Math.max(0, contentHeight - viewportHeight)) * scroll01);

        // 渲染所有卡片
        int drawY = viewportY - yOffset;
        for (EquipmentSetBranch branch : branches) {
            int cardHeight = calculateCardHeight(font, branch);

            // 检查卡片是否在可视区域内
            if (isCardVisible(drawY, cardHeight)) {
                boolean hovering = isCardHovered(mouseX, mouseY, drawY, cardHeight);

                // 渲染卡片
                renderCard(guiGraphics, branch, viewportX, drawY, hovering);

                // 渲染确认按钮
                int tooltipHeight = calculateTooltipHeight(font, branch);
                SpriteIconButton btn = branchToConfirmButton.get(branch);
                if (btn != null) {
                    int btnX = viewportX + TEXTURE_WIDTH - CONFIRM_BUTTON_SIZE - BUTTON_MARGIN;
                    int btnY = drawY + WEARABLE_CARD_WIDTH + tooltipHeight - CONFIRM_BUTTON_SIZE - BUTTON_MARGIN;
                    btn.setX(btnX);
                    btn.setY(btnY);
                    btn.setWidth(CONFIRM_BUTTON_SIZE);
                    btn.setHeight(CONFIRM_BUTTON_SIZE);
                    btn.render(guiGraphics, mouseX, mouseY, partialTick);
                }

                // 渲染展开按钮（位于顶部图标区域右侧）
                SpriteIconButton expandBtn = branchToExpandButton.get(branch);
                if (expandBtn != null) {
                    int expandX = viewportX + TEXTURE_WIDTH - WEARABLE_ICON_SIZE - ICON_SPACING - 1;
                    int expandY = drawY + 1;
                    expandBtn.setX(expandX);
                    expandBtn.setY(expandY);
                    expandBtn.setWidth(WEARABLE_ICON_SIZE);
                    expandBtn.setHeight(WEARABLE_ICON_SIZE);
                    expandBtn.render(guiGraphics, mouseX, mouseY, partialTick);
                }
            }

            drawY += cardHeight + CARD_SEPARATOR;
        }

        // 渲染图标悬停提示（需在无裁剪状态下绘制，避免背景被裁剪）
        Component hoveredDesc = getHoveredWearableDesc(mouseX, mouseY);
        if (hoveredDesc != null) {
            guiGraphics.disableScissor();
            guiGraphics.renderTooltip(font, hoveredDesc, mouseX, mouseY);
            // 恢复当前视口的裁剪区域，保证后续组件渲染不受影响
            guiGraphics.enableScissor(viewportX, viewportY, width, viewportHeight);
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (SpriteIconButton spriteButton : branchToConfirmButton.values()) {
            if (spriteButton.isMouseOver(mouseX, mouseY)) {
                return spriteButton.mouseClicked(mouseX, mouseY, button);
            }
        }
        for (SpriteIconButton spriteButton : branchToExpandButton.values()) {
            if (spriteButton.isMouseOver(mouseX, mouseY)) {
                return spriteButton.mouseClicked(mouseX, mouseY, button);
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    // ==================== 内部类 ====================
    // ==== 布局计算：内联方法 ====
    private int calculateTooltipHeight(Font font, EquipmentSetBranch branch) {
        Component effectComponent = buildBranchEffectComponent(branch);
        if (effectComponent == null) {
            return MIN_TOOLTIP_AREA;
        }
        List<FormattedCharSequence> lines = font.split(effectComponent, CARD_INNER_TEXT_WIDTH);
        int textHeight = Math.max(1, lines.size()) * font.lineHeight + TEXT_TOP_PADDING * 2;
        return Math.max(MIN_TOOLTIP_AREA, textHeight);
    }

    private Component buildBranchEffectComponent(EquipmentSetBranch branch) {
        if (branch == null) {
            return null;
        }
        ResourceLocation id = EquipmentSetManager.getInstance().getBranchResource(branch);
        if (id == null) {
            return null;
        }
        String key = EquipmentBenediction.asDescriptionId("equipment_set.branch_effect." + id.getNamespace() + "." + id.getPath().replace('/', '.'));
        return Component.translatable(key);
    }

    private int calculateCardHeight(Font font, EquipmentSetBranch branch) {
        int tooltipHeight = calculateTooltipHeight(font, branch);
        return WEARABLE_CARD_WIDTH + CARD_SEPARATOR + tooltipHeight + CARD_SEPARATOR + END_HEIGHT;
    }

    private int calculateTotalContentHeight(Font font, List<EquipmentSetBranch> branches) {
        int totalHeight = 0;
        for (EquipmentSetBranch branch : branches) {
            totalHeight += calculateCardHeight(font, branch) + CARD_SEPARATOR;
        }
        return totalHeight;
    }

    private boolean isCardVisible(int cardY, int cardHeight) {
        return cardY + cardHeight >= viewportY && cardY <= viewportY + viewportHeight;
    }

    private boolean isCardHovered(int mouseX, int mouseY, int cardY, int cardHeight) {
        return mouseX >= viewportX && mouseX < (viewportX + width)
                && mouseY >= cardY && mouseY < (cardY + cardHeight);
    }

    /**
     * 获取当前鼠标悬停的分支卡片
     */
    public EquipmentSetBranch getHoveredBranch(int mouseX, int mouseY) {
        if (equipmentSet == null) {
            return null;
        }

        Font font = Minecraft.getInstance().font;
        List<EquipmentSetBranch> branches = new ArrayList<>(equipmentSet.allBranch());
        int yOffset = (int) ((Math.max(0, contentHeight - viewportHeight)) * scroll01);
        int drawY = viewportY - yOffset;

        for (EquipmentSetBranch branch : branches) {
            int cardHeight = calculateCardHeight(font, branch);
            if (isCardVisible(drawY, cardHeight) && isCardHovered(mouseX, mouseY, drawY, cardHeight)) {
                return branch;
            }
            drawY += cardHeight + CARD_SEPARATOR;
        }
        return null;
    }

    /**
     * 获取当前鼠标悬停的装备描述
     */
    public Component getHoveredWearableDesc(int mouseX, int mouseY) {
        if (equipmentSet == null) {
            return null;
        }

        List<EquipmentSetBranch> branches = new ArrayList<>(equipmentSet.allBranch());
        Font font = Minecraft.getInstance().font;
        int yOffset = (int) ((Math.max(0, contentHeight - viewportHeight)) * scroll01);
        int drawY = viewportY - yOffset;

        for (EquipmentSetBranch branch : branches) {
            int cardHeight = calculateCardHeight(font, branch);

            if (isCardVisible(drawY, cardHeight)) {
                int iconX = viewportX + ICON_SPACING;
                int iconY = drawY + 1;

                int renderedCount = 0;
                for (IWearable wearable : branch.equipages().keySet()) {
                    ResourceLocation icon = wearable.getIcon();
                    if (icon != null) {
                        // 检查鼠标是否悬停在这个图标上
                        if (mouseX >= iconX && mouseX < iconX + WEARABLE_ICON_SIZE
                                && mouseY >= iconY && mouseY < iconY + WEARABLE_ICON_SIZE) {
                            return wearable.getDesc();
                        }
                    }

                    renderedCount++;
                    if (renderedCount >= WEARABLE_RENDER_MAX_COUNT) {
                        break;
                    }

                    iconX += WEARABLE_ICON_SIZE + ICON_SPACING;
                    if (iconX > viewportX + TEXTURE_WIDTH - WEARABLE_ICON_SIZE - ICON_SPACING) {
                        iconX = viewportX + ICON_SPACING;
                        iconY += WEARABLE_ICON_SIZE + 1;
                    }
                }
            }

            drawY += cardHeight + CARD_SEPARATOR;
        }

        return null;
    }

    /**
     * 渲染完整的卡片
     */
    public void renderCard(GuiGraphics guiGraphics, EquipmentSetBranch branch,
            int x, int y, boolean hovering) {
        int vOffset = hovering ? HOVER_OFFSET : 0;

        // 渲染顶部装备图标区域
        renderWearableCardBackground(guiGraphics, x, y, vOffset);
        renderWearableIcons(guiGraphics, branch, x, y);

        // 渲染工具提示区域（背景 + 文本）
        int tooltipAreaHeight = calculateTooltipHeight(Minecraft.getInstance().font, branch);
        renderTooltipBackground(guiGraphics, x, y, tooltipAreaHeight, vOffset);
        renderTooltipText(guiGraphics, branch, x, y);

        // 渲染底部边框
        renderBottomBorder(guiGraphics, x, y, tooltipAreaHeight, vOffset);
    }

    /**
     * 渲染可穿戴装备卡片背景
     */
    private void renderWearableCardBackground(GuiGraphics guiGraphics, int x, int y, int vOffset) {
        guiGraphics.blit(BACKGROUND_TEXTURE, x, y, 0, vOffset,
                TEXTURE_WIDTH, WEARABLE_CARD_WIDTH, TEXTURE_WIDTH, TEXTURE_HEIGHT);
    }

    /**
     * 渲染可穿戴装备图标按钮
     */
    private void renderWearableIcons(GuiGraphics guiGraphics, EquipmentSetBranch branch, int x, int y) {
        if (branch == null) {
            return;
        }

        int iconX = x + ICON_SPACING;
        int iconY = y + 1;

        int renderedCount = 0;
        for (IWearable wearable : branch.equipages().keySet()) {
            ResourceLocation icon = wearable.getIcon();
            if (icon != null) {
                guiGraphics.setColor(0.3f, 0.3f, 0.3f, 1.0f);
                guiGraphics.blit(icon, iconX + 1, iconY, WEARABLE_ICON_SIZE, WEARABLE_ICON_SIZE,
                        0, 0, WEARABLE_ICON_SIZE, WEARABLE_ICON_SIZE,
                        WEARABLE_ICON_SIZE, WEARABLE_ICON_SIZE);
                guiGraphics.setColor(1.0f, 1.0f, 1.0f, 1.0f); // 重置颜色
            }

            renderedCount++;
            if (renderedCount >= 8) {
                break;
            }

            iconX += WEARABLE_ICON_SIZE + ICON_SPACING;
            if (iconX > x + TEXTURE_WIDTH - WEARABLE_ICON_SIZE - ICON_SPACING) {
                iconX = x + ICON_SPACING;
                iconY += WEARABLE_ICON_SIZE + 1;
            }
        }
    }

    /**
     * 渲染工具提示背景
     */
    private void renderTooltipBackground(GuiGraphics guiGraphics, int x, int y, int tooltipAreaHeight, int vOffset) {
        guiGraphics.blit(BACKGROUND_TEXTURE, x, y + WEARABLE_CARD_WIDTH, TEXTURE_WIDTH, tooltipAreaHeight,
                0, WEARABLE_CARD_WIDTH + CARD_SEPARATOR + vOffset, TEXTURE_WIDTH, TOOLTIP_WIDTH,
                TEXTURE_WIDTH, TEXTURE_HEIGHT);
    }

    /**
     * 渲染工具提示文本（套装效果）
     */
    private void renderTooltipText(GuiGraphics guiGraphics, EquipmentSetBranch branch, int x, int y) {
        Component effectComponent = buildBranchEffectComponent(branch);
        if (effectComponent == null) {
            return;
        }

        Font font = Minecraft.getInstance().font;
        int textX = x + TEXT_LEFT_PADDING;
        int textY = y + WEARABLE_CARD_WIDTH + TEXT_TOP_PADDING;

        List<FormattedCharSequence> lines = font.split(effectComponent, CARD_INNER_TEXT_WIDTH);
        for (FormattedCharSequence line : lines) {
            guiGraphics.drawString(font, line, textX, textY, 0xFFEFEFEF);
            textY += font.lineHeight;
        }
    }

    /**
     * 渲染底部边框
     */
    private void renderBottomBorder(GuiGraphics guiGraphics, int x, int y, int tooltipAreaHeight, int vOffset) {
        guiGraphics.blit(BACKGROUND_TEXTURE, x, y + WEARABLE_CARD_WIDTH + tooltipAreaHeight + CARD_SEPARATOR,
                TEXTURE_WIDTH, END_HEIGHT, 0,
                WEARABLE_CARD_WIDTH + TOOLTIP_WIDTH + END_HEIGHT + CARD_SEPARATOR + vOffset,
                TEXTURE_WIDTH, END_HEIGHT, TEXTURE_WIDTH, TEXTURE_HEIGHT);
    }
}
