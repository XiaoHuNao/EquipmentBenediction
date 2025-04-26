package com.xiaohunao.equipment_benediction.client.gui.screen.switcher;

import com.xiaohunao.equipment_benediction.EquipmentBenediction;
import com.xiaohunao.equipment_benediction.client.gui.widget.EquippableSetButton;
import com.xiaohunao.equipment_benediction.client.gui.widget.SetTitleButton;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSet;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquippableSetData;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import java.util.Set;

public class SwitcherModuleUI {
    public static final ResourceLocation EQUIPMENT_SET_SWITCHER = EquipmentBenediction.asResource("textures/gui/set_switcher/equipment_set_switcher.png");

    public static final int WIDTH = 176;
    public static final int HEIGHT = 86;

    private ArmorStandPreviewUI previewUI;
    private SetButtonUI setButtonUI;
    private ScrollUI scrollUI;
    private final Player player;

    public int leftPos;
    public int topPos;
    private EquippableSetButton hoveredButton = null;
    private Set<EquipmentSet> currentEquipmentSets;

    public SwitcherModuleUI(Player player, int leftPos, int topPos) {
        this.player = player;
        this.leftPos = leftPos;
        this.topPos = topPos;
        
        ArmorStand armorStand = createArmorStand();
        this.previewUI = new ArmorStandPreviewUI(player, armorStand, 
            leftPos + 120, topPos + 10, 
            leftPos + 170, topPos + 80);
        this.setButtonUI = new SetButtonUI(player, leftPos, topPos);
        this.scrollUI = new ScrollUI(leftPos, topPos);
    }

    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Render background
        guiGraphics.blit(EQUIPMENT_SET_SWITCHER, leftPos, topPos, 0, 0, WIDTH, HEIGHT);
        
        // Render content
        enableScissor(guiGraphics);
        hoveredButton = null;
        for (EquippableSetButton button : setButtonUI.getSetButtons()) {
            if (button.isHovered()) {
                hoveredButton = button;
                break;
            }
        }
        
        setButtonUI.renderEquipmentSets(guiGraphics, mouseX, mouseY, scrollUI.getScrollOffset(), partialTick);
        guiGraphics.disableScissor();
        
        scrollUI.renderScrollBar(guiGraphics);
        
        if (hoveredButton != null) {
            Set<EquippableSetData> previewSet = Set.of(hoveredButton.getSetData());
            previewUI.render(guiGraphics, previewSet);
        } else {
            previewUI.renderPlayerEquipment(guiGraphics, player);
        }
    }

    private void enableScissor(GuiGraphics guiGraphics) {
        int x = leftPos + EquipmentSetSwitcherScreen.CONTENT_AREA_X;
        int y = topPos + EquipmentSetSwitcherScreen.CONTENT_AREA_Y;
        guiGraphics.enableScissor(
            x, y, 
            x + EquipmentSetSwitcherScreen.CONTENT_AREA_WIDTH, 
            y + EquipmentSetSwitcherScreen.CONTENT_AREA_HEIGHT
        );
    }

    private ArmorStand createArmorStand() {
        ArmorStand armorStand = new ArmorStand(player.level(), 0, 0, 0);
        armorStand.setShowArms(true);
        armorStand.setNoBasePlate(true);
        return armorStand;
    }

    public void setPosition(int leftPos, int topPos) {
        this.leftPos = leftPos;
        this.topPos = topPos;

        if (previewUI != null) {
            this.previewUI = new ArmorStandPreviewUI(
                player,
                createArmorStand(),
                leftPos + 120, topPos + 10,
                leftPos + 170, topPos + 80
            );
        }
        
        if (setButtonUI != null) {
            this.setButtonUI = new SetButtonUI(player, leftPos, topPos);
            if (currentEquipmentSets != null) {
                this.setButtonUI.initButtons(currentEquipmentSets);
            }
        }
        
        if (scrollUI != null) {
            this.scrollUI = new ScrollUI(leftPos, topPos);
            scrollUI.setScrollOffset(0.0F);
        }
    }

    public void initButtons(Set<EquipmentSet> equipmentSets) {
        this.currentEquipmentSets = equipmentSets;
        setButtonUI.initButtons(equipmentSets);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        System.out.println("SwitcherModuleUI mouseClicked: x=" + mouseX + ", y=" + mouseY + ", button=" + button);
        
        if (button == 0) {
            int scrollBarX = leftPos + 108;
            int scrollBarY = topPos + 8;
            if (mouseX >= scrollBarX && mouseX < scrollBarX + scrollUI.getScrollBarWidth() &&
                mouseY >= scrollBarY && mouseY < scrollBarY + scrollUI.getScrollAreaHeight()) {
                scrollUI.setScrolling(true);
                return true;
            }

            // 检查标题按钮的点击
            for (SetTitleButton titleButton : setButtonUI.getTitleButtons()) {
                if (titleButton.isMouseOver(mouseX, mouseY)) {
                    System.out.println("Title button clicked!");
                    titleButton.mouseClicked(mouseX, mouseY, button);
                    return true;
                }
            }

            // 检查装备效果按钮的点击
            for (EquippableSetButton setButton : setButtonUI.getSetButtons()) {
                if (setButton.isMouseOver(mouseX, mouseY)) {
                    if (setButton.isValidForPlayer(player)) {
                        setButton.onPress();
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            scrollUI.setScrolling(false);
            return true;
        }
        return false;
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (scrollUI.isScrolling() && setButtonUI.getMaxScroll() > 0) {
            int scrollBarY = topPos + 8;
            float dragDistance = ((float) mouseY - scrollBarY) / (float) (scrollUI.getScrollAreaHeight() - scrollUI.getScrollBarHeight());
            scrollUI.setScrollOffset(Math.min(Math.max(dragDistance, 0.0F), 1.0F));
            return true;
        }
        return false;
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (setButtonUI.getMaxScroll() > 0) {
            float scrollAmount = 10.0F / setButtonUI.getMaxScroll();
            scrollUI.setScrollOffset(Math.min(Math.max(scrollUI.getScrollOffset() - (float)verticalAmount * scrollAmount, 0.0F), 1.0F));
            return true;
        }
        return false;
    }

    public void removed() {
    }
}