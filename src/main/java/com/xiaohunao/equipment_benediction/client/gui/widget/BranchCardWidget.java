package com.xiaohunao.equipment_benediction.client.gui.widget;

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
import net.minecraft.world.item.crafting.Ingredient;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class BranchCardWidget extends AbstractWidget {
    public static final ResourceLocation BACKGROUND_TEXTURE = EquipmentBenediction.asResource("textures/gui/baranch_card.png");
    public static final ResourceLocation CONFIRM_TEXTURE_BUTTON = EquipmentBenediction.asResource("textures/gui/confirm_button.png");
    public static final ResourceLocation SLOT_EXPAND_BUTTON = EquipmentBenediction.asResource("textures/gui/slot_expand_button.png");

    public static final int TEXTURE_WIDTH = 100;
    public static final int TEXTURE_HEIGHT = 120;

    public static final int CARD_SIDE_HEIGHT = 1;
    public static final int CARD_BACKGROUND_HEIGHT = 1;
    public static final int CARD_SLOT_HEIGHT = 1;
    public static final int HOVER_OFFSET = 7;
    public static final int WEARABLE_ICON_SIZE = 9;
    private static final int CONFIRM_BUTTON_SIZE = 9;
    private static final int EXPAND_BUTTON_SIZE = 7;

    public static final int SLOT_SIZE = 18;
    public static final int CARD_INNER_TEXT_WIDTH = 80;

    private EquipmentSet selectedEquipmentSet;
    private double scroll01;

    private final LinkedList<EquipmentSetBranch> branches = new LinkedList<>();
    private final Map<EquipmentSetBranch, SpriteIconButton> branchToConfirmButton = new LinkedHashMap<>();
    private final Map<EquipmentSetBranch, SpriteIconButton> branchToExpandButton = new LinkedHashMap<>();



    public BranchCardWidget(int x, int y) {
        super(x, y, 0, 0, Component.empty());
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (selectedEquipmentSet == null) {
            return;
        }

        EquipmentSetBranch branch = branches.getFirst();
        renderCard(guiGraphics, branch,0, mouseX, mouseY, partialTick);
    }

    private void renderCard(GuiGraphics guiGraphics, EquipmentSetBranch branch,int hover_offset,int mouseX, int mouseY, float partialTick){
        int x = this.getX();
        int currentY = this.getY();

        //渲染顶边
        guiGraphics.blit(BACKGROUND_TEXTURE, x, currentY, TEXTURE_WIDTH, CARD_SIDE_HEIGHT, 0,5,TEXTURE_WIDTH,0,TEXTURE_WIDTH,TEXTURE_HEIGHT);
        currentY += CARD_SIDE_HEIGHT;

        //渲染Slot槽
        SpriteIconButton spriteIconButton = branchToExpandButton.get(branch);
        LinkedHashMap<IWearable, Ingredient> equipages = branch.equipages();

        if (spriteIconButton.isSelected()){
            //            guiGraphics.blit(BACKGROUND_TEXTURE, x, y + CARD_SIDE_HEIGHT, TEXTURE_WIDTH, expandTableHeight,0,CARD_SIDE_HEIGHT,0,CARD_SLOT_HEIGHT,TEXTURE_WIDTH,TEXTURE_HEIGHT);
            List<IWearable> blacklist = branch.blacklist();
        }else {
            guiGraphics.blit(BACKGROUND_TEXTURE, x, currentY, TEXTURE_WIDTH, WEARABLE_ICON_SIZE + 1,0,0,TEXTURE_WIDTH,0,TEXTURE_WIDTH,TEXTURE_HEIGHT);
            int minSlotCount = Math.min(5, equipages.size());
            int wearableCount = 0;
            for (IWearable wearable : equipages.keySet()) {
                ResourceLocation icon = wearable.getIcon();
                guiGraphics.blit(icon, x + wearableCount * WEARABLE_ICON_SIZE + 2, currentY, WEARABLE_ICON_SIZE,WEARABLE_ICON_SIZE,0, 0, WEARABLE_ICON_SIZE, WEARABLE_ICON_SIZE, WEARABLE_ICON_SIZE,WEARABLE_ICON_SIZE);
                wearableCount++;

                if (wearableCount >= minSlotCount) {
                    break;
                }
            }
            SpriteIconButton expandBtn = branchToExpandButton.get(branch);
            if (expandBtn != null){
                int expandBtnX = x + TEXTURE_WIDTH - WEARABLE_ICON_SIZE - 2;
                int expandBtnY = currentY + 1;
                expandBtn.setPosition(expandBtnX, expandBtnY);
                expandBtn.render(guiGraphics, mouseX, mouseY, partialTick);
            }

            currentY += WEARABLE_ICON_SIZE;
            guiGraphics.blit(BACKGROUND_TEXTURE, x, currentY, TEXTURE_WIDTH, CARD_SLOT_HEIGHT,0,3,TEXTURE_WIDTH,0,TEXTURE_WIDTH,TEXTURE_HEIGHT);
        }

        //渲染套装描述
        currentY += CARD_SLOT_HEIGHT;
        int textX = x + 4;
        int textY = currentY + 4;
        ResourceLocation branchResource = EquipmentSetManager.getInstance().getBranchResource(branch);
        Component description = Component.translatable(EquipmentBenediction.asDescriptionId("equipment_set.branch." + branchResource.getNamespace() + "." + branchResource.getPath().replace('/', '.')));
        Font font = Minecraft.getInstance().font;
        List<FormattedCharSequence> lines = font.split(description, CARD_INNER_TEXT_WIDTH);
        int textHeight = lines.size() * font.lineHeight;
        //渲染文本背景
        guiGraphics.blit(BACKGROUND_TEXTURE, x, currentY, TEXTURE_WIDTH, textHeight,0,0,TEXTURE_WIDTH,0,TEXTURE_WIDTH,TEXTURE_HEIGHT);
        //渲染确认按钮背景
        currentY += textHeight;
        guiGraphics.blit(BACKGROUND_TEXTURE, x, currentY, TEXTURE_WIDTH, CONFIRM_BUTTON_SIZE + 1,0,0,TEXTURE_WIDTH,0,TEXTURE_WIDTH,TEXTURE_HEIGHT);
        for (int i = 0; i < lines.size(); i++) {
            FormattedCharSequence line = lines.get(i);
            guiGraphics.drawString(font, line, textX, textY + i * font.lineHeight, 0xFFEFEFEF, false);
        }
        SpriteIconButton confirmBtn = branchToConfirmButton.get(branch);
        if (confirmBtn != null){
            int confirmBtnX = x + TEXTURE_WIDTH - WEARABLE_ICON_SIZE - CONFIRM_BUTTON_SIZE / 2;
            confirmBtn.setPosition(confirmBtnX, currentY);
            confirmBtn.render(guiGraphics, mouseX, mouseY, partialTick);
        }
        //渲染底边
        currentY += CONFIRM_BUTTON_SIZE + CARD_SIDE_HEIGHT;
        guiGraphics.blit(BACKGROUND_TEXTURE, x, currentY, TEXTURE_WIDTH ,CARD_SIDE_HEIGHT, 0,5,TEXTURE_WIDTH,0,TEXTURE_WIDTH,TEXTURE_HEIGHT);
    }


    public void setEquipmentSet(EquipmentSet equipmentSet) {
        if (this.selectedEquipmentSet == equipmentSet && !this.branchToConfirmButton.isEmpty()) {
            return;
        }

        this.selectedEquipmentSet = equipmentSet;
        this.branches.clear();
        this.branchToConfirmButton.clear();
        this.branchToExpandButton.clear();

        if (equipmentSet != null) {

            for (EquipmentSetBranch branch : equipmentSet.allBranch()) {
                branches.add(branch);

                SpriteIconButton btn = new SpriteIconButton.Builder(button -> {
                })
                        .bounds(0, 0, CONFIRM_BUTTON_SIZE,CONFIRM_BUTTON_SIZE)
                        .sprite(CONFIRM_TEXTURE_BUTTON)
                        .enableClickAnimation(true)
                        .stateFrame(SpriteIconButton.ButtonVisualState.NORMAL, 0)
                        .stateFrame(SpriteIconButton.ButtonVisualState.SELECTED, 1)
                        .build();
                branchToConfirmButton.put(branch, btn);


                SpriteIconButton expandBtn = new SpriteIconButton.Builder(button -> {

                })
                        .bounds(0, 0, EXPAND_BUTTON_SIZE,EXPAND_BUTTON_SIZE)
                        .sprite(SLOT_EXPAND_BUTTON)
                        .enableClickAnimation(true)
                        .stateFrame(SpriteIconButton.ButtonVisualState.NORMAL, 0)
                        .stateFrame(SpriteIconButton.ButtonVisualState.SELECTED, 1)
                        .build();
                branchToExpandButton.put(branch, expandBtn);
            }
        }
    }

    public void setScroll01(double scroll01) {
        this.scroll01 = Math.max(0.0, Math.min(1.0, scroll01));
    }

    public EquipmentSetBranch getHoveredBranch(int mouseX, int mouseY) {
        return null;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

}
