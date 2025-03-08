package com.xiaohunao.equipment_benediction.client.gui.screen.switcher;

import com.google.common.collect.Sets;
import com.xiaohunao.equipment_benediction.EquipmentBenediction;
import com.xiaohunao.equipment_benediction.api.manager.EquipmentSetManager;
import com.xiaohunao.equipment_benediction.common.attachment.EntityHookManager;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSet;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquippableSetData;
import com.xiaohunao.equipment_benediction.common.init.EBAttachments;
import com.xiaohunao.equipment_benediction.client.gui.widget.EquippableSetButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.util.Mth;

import java.util.Set;

public class EquipmentSetSwitcherScreen extends Screen {
    public static final ResourceLocation EQUIPMENT_SET_SWITCHER = EquipmentBenediction.asResource("textures/gui/set_switcher/equipment_set_switcher.png");

    public static final ResourceLocation EXTRA_SLOTS = EquipmentBenediction.asResource("textures/gui/set_switcher/extra_slots.png");
    public static final ResourceLocation SET_SHOW_ALL_CLOSE = EquipmentBenediction.asResource("textures/gui/set_switcher/set_show_all_close.png");
    public static final ResourceLocation SET_SHOW_ALL_OPEN = EquipmentBenediction.asResource("textures/gui/set_switcher/set_show_all_open.png");
    public static final ResourceLocation SET_SHOW_ALL_CLOSE_HOVERED = EquipmentBenediction.asResource("textures/gui/set_switcher/set_show_all_close_hovered.png");
    public static final ResourceLocation SET_SHOW_ALL_OPEN_HOVERED = EquipmentBenediction.asResource("textures/gui/set_switcher/set_show_all_open_hovered.png");

    public static final int IMAGE_WIDTH = 176;
    public static final int IMAGE_HEIGHT = 86;

    public static final int CONTENT_AREA_X = 8;
    public static final int CONTENT_AREA_Y = 8;
    public static final int CONTENT_AREA_WIDTH = 92;
    public static final int CONTENT_AREA_HEIGHT = 70;

    private static final int SHOW_ALL_BUTTON_WIDTH = 16;
    private static final int SHOW_ALL_BUTTON_HEIGHT = 16;

    private final Player player;
    private int leftPos;
    private int topPos;
    
    private ArmorStandPreviewUI previewManager;
    private SetButtonUI equipmentSetManager;
    private ScrollUI scrollUI;
    private boolean showAllSet = true;
    private EquippableSetButton hoveredButton = null;

    public EquipmentSetSwitcherScreen(Player player) {
        super(Component.translatable("screen.equipment_benediction.equipment_set_switcher"));
        this.player = player;
    }

    @Override
    protected void init() {
        super.init();
        this.leftPos = (this.width - IMAGE_WIDTH) / 2;
        this.topPos = (this.height - IMAGE_HEIGHT) / 2;

        initManagers();
        equipmentSetManager.initButtons(getShowSet());
    }

    private void initManagers() {
        if (minecraft != null && minecraft.level != null) {
            previewManager = new ArmorStandPreviewUI(player, createArmorStand(),
                leftPos + 120, topPos + 10, leftPos + 170, topPos + 80);
        }
        equipmentSetManager = new SetButtonUI(player,leftPos, topPos);
        scrollUI = new ScrollUI(leftPos, topPos);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        renderMainGui(guiGraphics);
        renderContent(guiGraphics, mouseX, mouseY, partialTick);
    }

    private void renderMainGui(GuiGraphics guiGraphics) {
        guiGraphics.blit(EQUIPMENT_SET_SWITCHER, leftPos, topPos, 0, 0,
            IMAGE_WIDTH, IMAGE_HEIGHT);
    }

    private void renderContent(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        enableScissor(guiGraphics);
        hoveredButton = null;
        for (EquippableSetButton button : equipmentSetManager.getSetButtons()) {
            if (button.isHovered()) {
                hoveredButton = button;
                break;
            }
        }
        
        equipmentSetManager.renderEquipmentSets(guiGraphics, mouseX, mouseY, 
            scrollUI.getScrollOffset(), partialTick);
        guiGraphics.disableScissor();
        
        scrollUI.renderScrollBar(guiGraphics);
        
        if (hoveredButton != null) {
            Set<EquippableSetData> previewSet = Sets.newHashSet();
            previewSet.add(hoveredButton.getSetData());
            previewManager.render(guiGraphics, previewSet);
        } else {
            previewManager.renderPlayerEquipment(guiGraphics, player);
        }
        
        renderShowAllButton(guiGraphics, mouseX, mouseY);
    }

    private void renderShowAllButton(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int buttonX = leftPos - SHOW_ALL_BUTTON_WIDTH;
        int buttonY = topPos;
        
        boolean isHovered = mouseX >= buttonX && mouseX < buttonX + SHOW_ALL_BUTTON_WIDTH 
            && mouseY >= buttonY && mouseY < buttonY + SHOW_ALL_BUTTON_HEIGHT;
            
        ResourceLocation texture = showAllSet ? 
            (isHovered ? SET_SHOW_ALL_OPEN_HOVERED : SET_SHOW_ALL_OPEN) :
            (isHovered ? SET_SHOW_ALL_CLOSE_HOVERED : SET_SHOW_ALL_CLOSE);


        guiGraphics.blit(texture, buttonX, buttonY, 0, 0, SHOW_ALL_BUTTON_WIDTH, SHOW_ALL_BUTTON_HEIGHT,SHOW_ALL_BUTTON_WIDTH, SHOW_ALL_BUTTON_HEIGHT);


    }

    private ArmorStand createArmorStand() {
        ArmorStand armorStand = new ArmorStand(player.level(), 0, 0, 0);
        armorStand.setShowArms(true);
        armorStand.setNoBasePlate(true);

        return armorStand;
    }

    private void enableScissor(GuiGraphics guiGraphics) {
        int x = leftPos + CONTENT_AREA_X;
        int y = topPos  + CONTENT_AREA_Y;
        guiGraphics.enableScissor(x, y, x + CONTENT_AREA_WIDTH, y + CONTENT_AREA_HEIGHT);
    }

    private Set<EquipmentSet> getShowSet(){
        Set<EquipmentSet> equipmentSets = Sets.newHashSet();

        if (showAllSet){
            equipmentSets.addAll(EquipmentSetManager.getInstance().getAllResources().values());
            return equipmentSets;
        }

        EntityHookManager entityHookManager = player.getData(EBAttachments.ENTITY_HOOK_MANAGER);
        equipmentSets.addAll(entityHookManager.getEquipmentSetHookMap().keySet());
        return equipmentSets;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void removed() {
        super.removed();
        this.previewManager = null;  // 清理盔甲架实例
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {  // 左键点击
            // 更新按钮点击检测位置
            int buttonX = leftPos - SHOW_ALL_BUTTON_WIDTH - 2;
            int buttonY = topPos;
            if (mouseX >= buttonX && mouseX < buttonX + SHOW_ALL_BUTTON_WIDTH &&
                mouseY >= buttonY && mouseY < buttonY + SHOW_ALL_BUTTON_HEIGHT) {
                showAllSet = !showAllSet;
                equipmentSetManager.initButtons(getShowSet());
                return true;
            }

            // 检查滚动条点击
            int scrollBarX = leftPos + 108;
            int scrollBarY = topPos + 8;
            if (mouseX >= scrollBarX && mouseX < scrollBarX + scrollUI.getScrollBarWidth() &&
                mouseY >= scrollBarY && mouseY < scrollBarY + scrollUI.getScrollAreaHeight()) {
                scrollUI.setScrolling(true);
                return true;
            }

            // 检查按钮点击
            for (EquippableSetButton button1 : equipmentSetManager.getSetButtons()) {
                if (button1.isMouseOver(mouseX, mouseY)) {
                    if (button1.isValidForPlayer(player)) {
                        button1.onPress();
                    }
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            scrollUI.setScrolling(false);
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (scrollUI.isScrolling() && equipmentSetManager.getMaxScroll() > 0) {
            int scrollBarY = topPos + 8;
            float dragDistance = ((float) mouseY - scrollBarY) / (float) (scrollUI.getScrollAreaHeight() - scrollUI.getScrollBarHeight());
            scrollUI.setScrollOffset(Mth.clamp(dragDistance, 0.0F, 1.0F));
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (equipmentSetManager.getMaxScroll() > 0) {
            float scrollAmount = 10.0F / equipmentSetManager.getMaxScroll();
            scrollUI.setScrollOffset(Mth.clamp(scrollUI.getScrollOffset() - (float)verticalAmount * scrollAmount, 0.0F, 1.0F));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }
} 