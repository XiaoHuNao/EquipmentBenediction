package com.xiaohunao.equipment_benediction.client.gui.widget;

import java.util.*;

import com.xiaohunao.equipment_benediction.EquipmentBenediction;
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
import net.minecraft.world.item.crafting.Ingredient;

public class BranchCardWidget extends AbstractWidget {

    public static final ResourceLocation BACKGROUND_TEXTURE = EquipmentBenediction.asResource("textures/gui/baranch_card.png");
    public static final ResourceLocation CONFIRM_TEXTURE = EquipmentBenediction.asResource("textures/gui/confirm_button.png");

    public static final int TEXTURE_WIDTH = 100;
    public static final int TEXTURE_HEIGHT = 30;
    public static final int HEIGHT = 15;
    public static final int HOVER_OFFSET = HEIGHT;
    public static final int WEARABLE_ICON_SIZE = 9;
    public static final int WEARABLE_CARD_WIDTH = WEARABLE_ICON_SIZE + 2;
    public static final int TOOLTIP_WIDTH = 1;
    public static final int END_HEIGHT = 1;
    public static final int SEP = 1;

    private static final int TEXT_LEFT_PADDING = 4;
    private static final int TEXT_TOP_PADDING = 3;
    // private static final int CONFIRM_SIZE = 9; // 预留：确认按钮尺寸（暂未使用）
    private static final int CARD_INNER_TEXT_WIDTH = 80; // 每行最大宽度

    public EquipmentSet equipmentSet;

    private int contentHeight;
    private int viewportX;
    private int viewportY;
    private int viewportHeight;
    private double scroll01; // 0..1

    // 每个分支对应一个确认按钮
    private final Map<EquipmentSetBranch, SpriteIconButton> branchToConfirmButton = new LinkedHashMap<>();

    public BranchCardWidget(int x, int y) {
        super(x, y, 0, 0, Component.empty());
    }

    public void setViewport(int x, int y, int width, int height) {
        this.viewportX = x;
        this.viewportY = y;
        this.viewportHeight = height;
        this.setX(x);
        this.setY(y);
        this.width = width;
        this.height = height;
    }

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

        if (equipmentSet != null) {
            List<EquipmentSetBranch> branches = new ArrayList<>(equipmentSet.allBranch());
            for (EquipmentSetBranch branch : branches) {
                SpriteIconButton btn = new SpriteIconButton.Builder(button -> {
                })
                        .bounds(0, 0, 9, 9)
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
            }
        }
    }

    public void setScroll01(double scroll01) {
        this.scroll01 = Math.max(0.0, Math.min(1.0, scroll01));
    }

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
        int yOffset = (int) ((Math.max(0, contentHeight - viewportHeight)) * scroll01);

        contentHeight = 0;

        List<EquipmentSetBranch> branches = new ArrayList<>(equipmentSet.allBranch());

        // 首先测量总高度
        for (EquipmentSetBranch branch : branches) {
            int tooltipHeight = calcTooltipHeight(font, branch);
            int cardHeight = WEARABLE_CARD_WIDTH + SEP + tooltipHeight + SEP + END_HEIGHT;
            contentHeight += cardHeight + SEP;
        }

        // 实际渲染（考虑滚动与可视区域）
        int drawY = viewportY - yOffset;
        for (EquipmentSetBranch branch : branches) {
            int tooltipHeight = calcTooltipHeight(font, branch);
            int cardHeight = WEARABLE_CARD_WIDTH + SEP + tooltipHeight + SEP + END_HEIGHT;

            // 可视裁剪判断
            if (drawY + cardHeight >= viewportY && drawY <= viewportY + viewportHeight) {
                boolean hovering = mouseX >= viewportX && mouseX < (viewportX + this.width)
                        && mouseY >= drawY && mouseY < (drawY + cardHeight);
                renderCard(guiGraphics, branch, mouseX, mouseY, viewportX, drawY, hovering);

                // 渲染并定位确认按钮（右下角）
                SpriteIconButton btn = branchToConfirmButton.get(branch);
                if (btn != null) {
                    int btnSize = 9; // 贴图一帧尺寸（像素）
                    int btnX = viewportX + TEXTURE_WIDTH - btnSize - 2;
                    int btnY = drawY + WEARABLE_CARD_WIDTH + calcTooltipHeight(font, branch) - btnSize - 2;
                    btn.setX(btnX);
                    btn.setY(btnY);
                    btn.setWidth(btnSize);
                    btn.setHeight(btnSize);
                    btn.render(guiGraphics, mouseX, mouseY, partialTick);
                }
            }

            drawY += cardHeight + SEP;
        }
    }

    private int calcTooltipHeight(Font font, EquipmentSetBranch branch) {
        int lineHeight = font.lineHeight;
        int lines = 0;
        if (branch != null) {
            for (Map.Entry<IWearable, Ingredient> entry : branch.equipages().entrySet()) {
                Component desc = entry.getKey().getDesc();
                if (desc != null) {
                    List<FormattedCharSequence> seq = font.split(desc, CARD_INNER_TEXT_WIDTH);
                    lines += Math.max(1, seq.size());
                }
            }
        }
        int textHeight = lines * lineHeight + TEXT_TOP_PADDING * 2;
        int minArea = 12; // 给确认按钮的最小空间
        return Math.max(minArea, textHeight);
    }

    public void renderCard(GuiGraphics guiGraphics, EquipmentSetBranch branch, int mouseX, int mouseY, int x, int y, boolean hovering) {
        int vOffset = hovering ? HOVER_OFFSET : 0;

        // 渲染IWearable背景（顶部条）
        guiGraphics.blit(BACKGROUND_TEXTURE, x, y, 0, vOffset, TEXTURE_WIDTH, WEARABLE_CARD_WIDTH, TEXTURE_WIDTH, TEXTURE_HEIGHT);

        // 渲染IWearable图标（横向排列）
        if (branch != null) {
            int iconX = x + 2;
            int iconY = y + 1;
            for (IWearable wearable : branch.equipages().keySet()) {
                ResourceLocation icon = wearable.getIcon();
                if (icon != null) {
                    guiGraphics.setColor(0.7f, 0.7f, 0.7f, 0.6f);
                    guiGraphics.blit(icon, iconX, iconY, WEARABLE_ICON_SIZE, WEARABLE_ICON_SIZE, 0, 0, WEARABLE_ICON_SIZE, WEARABLE_ICON_SIZE, WEARABLE_ICON_SIZE, WEARABLE_ICON_SIZE);
                    guiGraphics.setColor(1.0f, 1.0f, 1.0f, 1.0f); // 重置颜色
                }
                iconX += WEARABLE_ICON_SIZE + 2;
                if (iconX > x + TEXTURE_WIDTH - WEARABLE_ICON_SIZE - 2) {
                    iconX = x + 2;
                    iconY += WEARABLE_ICON_SIZE + 1;
                }
            }
        }

        // 渲染分支提示信息背景块
        int tooltipAreaHeight = calcTooltipHeight(Minecraft.getInstance().font, branch);
        guiGraphics.blit(BACKGROUND_TEXTURE, x, y + WEARABLE_CARD_WIDTH, TEXTURE_WIDTH, tooltipAreaHeight, 0, WEARABLE_CARD_WIDTH + SEP + vOffset, TEXTURE_WIDTH, TOOLTIP_WIDTH, TEXTURE_WIDTH, TEXTURE_HEIGHT);

        // 渲染文本内容
        Font font = Minecraft.getInstance().font;
        int textX = x + TEXT_LEFT_PADDING;
        int textY = y + WEARABLE_CARD_WIDTH + TEXT_TOP_PADDING;
        if (branch != null) {
            for (Map.Entry<IWearable, Ingredient> entry : branch.equipages().entrySet()) {
                Component desc = entry.getKey().getDesc();
                if (desc != null) {
                    List<FormattedCharSequence> seq = font.split(desc, CARD_INNER_TEXT_WIDTH);
                    for (FormattedCharSequence line : seq) {
                        guiGraphics.drawString(font, line, textX, textY, 0xFFEFEFEF);
                        textY += font.lineHeight;
                    }
                }
            }
        }

        // 渲染确认按钮（由外部列表统一调用 .render）此处只预留位置
        // 最底部封边
        guiGraphics.blit(BACKGROUND_TEXTURE, x, y + WEARABLE_CARD_WIDTH + tooltipAreaHeight + SEP, TEXTURE_WIDTH, END_HEIGHT, 0, WEARABLE_CARD_WIDTH + TOOLTIP_WIDTH + END_HEIGHT + SEP + vOffset, TEXTURE_WIDTH, END_HEIGHT, TEXTURE_WIDTH, TEXTURE_HEIGHT);

    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean handled = false;
        for (SpriteIconButton spriteButton : branchToConfirmButton.values()) {
            // 只将事件传递给当前可见范围内的按钮
            if (spriteButton.isMouseOver(mouseX, mouseY)) {
                handled |= spriteButton.mouseClicked(mouseX, mouseY, button);
            }
        }
        return handled || super.mouseClicked(mouseX, mouseY, button);
    }

}
