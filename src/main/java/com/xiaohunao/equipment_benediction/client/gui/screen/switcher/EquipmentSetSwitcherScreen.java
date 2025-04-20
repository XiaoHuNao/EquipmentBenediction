package com.xiaohunao.equipment_benediction.client.gui.screen.switcher;

import com.google.common.collect.Sets;
import com.xiaohunao.equipment_benediction.EquipmentBenediction;
import com.xiaohunao.equipment_benediction.api.manager.EquipmentSetManager;
import com.xiaohunao.equipment_benediction.common.attachment.EntityHookManager;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSet;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquippableSetData;
import com.xiaohunao.equipment_benediction.common.equippable.IEquippable;
import com.xiaohunao.equipment_benediction.common.init.EBAttachments;
import com.xiaohunao.equipment_benediction.client.gui.widget.EquippableSetButton;
import com.xiaohunao.equipment_benediction.common.init.EBRegistries;
import com.xiaohunao.equipment_benediction.client.gui.widget.LayoutControlButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.util.Mth;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.Map;
import java.util.Set;

public class EquipmentSetSwitcherScreen extends Screen {
    public static final ResourceLocation EQUIPMENT_SET_SWITCHER = EquipmentBenediction.asResource("textures/gui/set_switcher/equipment_set_switcher.png");

    public static final ResourceLocation EXTRA_SLOTS = EquipmentBenediction.asResource("textures/gui/set_switcher/extra_slots.png");
    public static final ResourceLocation SET_SHOW_ALL_CLOSE = EquipmentBenediction.asResource("textures/gui/set_switcher/set_show_all_close.png");
    public static final ResourceLocation SET_SHOW_ALL_OPEN = EquipmentBenediction.asResource("textures/gui/set_switcher/set_show_all_open.png");
    public static final ResourceLocation SET_SHOW_ALL_CLOSE_HOVERED = EquipmentBenediction.asResource("textures/gui/set_switcher/set_show_all_close_hovered.png");
    public static final ResourceLocation SET_SHOW_ALL_OPEN_HOVERED = EquipmentBenediction.asResource("textures/gui/set_switcher/set_show_all_open_hovered.png");

    public static final int BASE_IMAGE_WIDTH = 176;
    public static final int BASE_IMAGE_HEIGHT = 86;

    public static final int CONTENT_AREA_X = 8;
    public static final int CONTENT_AREA_Y = 8;
    public static final int CONTENT_AREA_WIDTH = 92;
    public static final int CONTENT_AREA_HEIGHT = 70;

    private static final int SHOW_ALL_BUTTON_WIDTH = 16;
    private static final int SHOW_ALL_BUTTON_HEIGHT = 16;

    private static final int LAYOUT_BUTTON_SPACING = 2;

    private int currentImageWidth = BASE_IMAGE_WIDTH;
    private int currentImageHeight = BASE_IMAGE_HEIGHT;
    
    private final Player player;
    private int leftPos;
    private int topPos;
    
    private ArmorStandPreviewUI previewManager;
    private SetButtonUI equipmentSetManager;
    private ScrollUI scrollUI;
    private boolean showAllSet = false;
    private EquippableSetButton hoveredButton = null;
    private LayoutControlButton layoutButton;
    private boolean isExpanded = false;

    public EquipmentSetSwitcherScreen(Player player) {
        super(Component.translatable("screen.equipment_benediction.equipment_set_switcher"));
        this.player = player;
    }

    @Override
    protected void init() {
        this.isExpanded = shouldExpandLayout();
        updateScreenSize();
        this.leftPos = (this.width - currentImageWidth) / 2;
        this.topPos = (this.height - currentImageHeight) / 2;

        initManagers();
        equipmentSetManager.initButtons(getShowSet());

        // 添加布局控制按钮，与"显示全部"按钮对齐
        int layoutButtonX = leftPos - SHOW_ALL_BUTTON_WIDTH;
        int layoutButtonY = topPos + SHOW_ALL_BUTTON_HEIGHT + LAYOUT_BUTTON_SPACING;
        
        layoutButton = new LayoutControlButton(
            layoutButtonX,
            layoutButtonY,
            button -> {
                this.isExpanded = !this.isExpanded;
                updateScreenSize();
                this.leftPos = (this.width - currentImageWidth) / 2;
                this.topPos = (this.height - currentImageHeight) / 2;
                initManagers();
                equipmentSetManager.initButtons(getShowSet());
            }
        );
    }

    private boolean shouldExpandLayout() {
        // 获取Minecraft窗口的实际大小
        int screenWidth = this.width;
        int screenHeight = this.height;

        // 计算4x4布局所需的最小空间
        int requiredWidth = BASE_IMAGE_WIDTH * 4;  // 4x4布局的宽度
        int requiredHeight = BASE_IMAGE_HEIGHT * 4; // 4x4布局的高度

        // 如果屏幕空间足够大，就返回true表示应该展开
        return screenWidth >= requiredWidth * 1.2 && screenHeight >= requiredHeight * 1.2;
    }

    private void updateScreenSize() {
        if (isExpanded) {
            // 计算可以容纳的最大网格大小
            int maxGridWidth = (this.width - 40) / BASE_IMAGE_WIDTH; // 留出一些边距
            int maxGridHeight = (this.height - 40) / BASE_IMAGE_HEIGHT;
            
            // 取较小值，确保不会超出屏幕
            int gridSize = Math.min(maxGridWidth, maxGridHeight);
            // 限制最大为4x4
            gridSize = Math.min(gridSize, 4);
            
            currentImageWidth = BASE_IMAGE_WIDTH * gridSize;
            currentImageHeight = BASE_IMAGE_HEIGHT * gridSize;
        } else {
            currentImageWidth = BASE_IMAGE_WIDTH;
            currentImageHeight = BASE_IMAGE_HEIGHT;
        }
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
        renderScreenBackground(guiGraphics, mouseX, mouseY, partialTick);
        renderContent(guiGraphics, mouseX, mouseY, partialTick);
    }

    private void renderScreenBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        
        // 根据是否展开决定渲染网格
        int gridSize = isExpanded ? Math.min((this.width - 40) / BASE_IMAGE_WIDTH, 4) : 1;
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                guiGraphics.blit(EQUIPMENT_SET_SWITCHER,
                    leftPos + col * BASE_IMAGE_WIDTH,
                    topPos + row * BASE_IMAGE_HEIGHT,
                    0, 0, BASE_IMAGE_WIDTH, BASE_IMAGE_HEIGHT);
            }
        }
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
        
        // 渲染所有模块的盔甲架
        if (isExpanded) {
            int gridSize = Math.min((this.width - 40) / BASE_IMAGE_WIDTH, 4);
            for (int row = 0; row < gridSize; row++) {
                for (int col = 0; col < gridSize; col++) {
                    int moduleX = leftPos + col * BASE_IMAGE_WIDTH;
                    int moduleY = topPos + row * BASE_IMAGE_HEIGHT;
                    
                    // 计算每个模块中盔甲架的位置
                    int armorStandX = moduleX + 120;
                    int armorStandY = moduleY + 10;
                    
                    if (hoveredButton != null) {
                        Set<EquippableSetData> previewSet = Sets.newHashSet();
                        previewSet.add(hoveredButton.getSetData());
                        previewManager.renderAt(guiGraphics, previewSet, armorStandX, armorStandY, armorStandX + 50, armorStandY + 70);
                    } else {
                        previewManager.renderPlayerEquipmentAt(guiGraphics, player, armorStandX, armorStandY, armorStandX + 50, armorStandY + 70);
                    }
                }
            }
        } else {
            // 原有的单个盔甲架渲染逻辑
            if (hoveredButton != null) {
                Set<EquippableSetData> previewSet = Sets.newHashSet();
                previewSet.add(hoveredButton.getSetData());
                previewManager.render(guiGraphics, previewSet);
            } else {
                previewManager.renderPlayerEquipment(guiGraphics, player);
            }
        }
        
        // 只在未展开时渲染按钮
        if (!isExpanded) {
            renderShowAllButton(guiGraphics, mouseX, mouseY);
            layoutButton.render(guiGraphics, mouseX, mouseY, partialTick);
        }
    }

    private void renderShowAllButton(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int buttonX = leftPos - SHOW_ALL_BUTTON_WIDTH;
        int buttonY = topPos;
        
        boolean isHovered = mouseX >= buttonX && mouseX < buttonX + SHOW_ALL_BUTTON_WIDTH 
            && mouseY >= buttonY && mouseY < buttonY + SHOW_ALL_BUTTON_HEIGHT;
            
        ResourceLocation texture = showAllSet ? 
            (isHovered ? SET_SHOW_ALL_OPEN_HOVERED : SET_SHOW_ALL_OPEN) :
            (isHovered ? SET_SHOW_ALL_CLOSE_HOVERED : SET_SHOW_ALL_CLOSE);

        guiGraphics.blit(texture, buttonX, buttonY, 0, 0, SHOW_ALL_BUTTON_WIDTH, SHOW_ALL_BUTTON_HEIGHT, SHOW_ALL_BUTTON_WIDTH, SHOW_ALL_BUTTON_HEIGHT);
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

    private Set<EquipmentSet> getShowSet() {
        Set<EquipmentSet> equipmentSets = Sets.newHashSet();
        EquipmentSetManager setManager = EquipmentSetManager.getInstance();

        if (showAllSet) {
            equipmentSets.addAll(setManager.getAllResources().values());
            return equipmentSets;
        }


        for (EquipmentSet set : setManager.getAllResources().values()) {
            setLoop:
            for (EquippableSetData branch : set.allBranch()) {
                for (Map.Entry<IEquippable, Ingredient> entry : branch.equipages().entrySet()) {
                    if (entry.getKey().checkEquippable(player, entry.getValue())) {
                        equipmentSets.add(set);
                        continue setLoop;
                    }
                }
            }
        }

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
        if (button == 0) {
            // 只在未展开时检查按钮点击
            if (!isExpanded) {
                // 检查布局按钮点击
                if (layoutButton.isMouseOver(mouseX, mouseY)) {
                    layoutButton.mouseClicked(mouseX, mouseY, button);
                    return true;
                }

                // 检查显示全部按钮点击
                int buttonX = leftPos - SHOW_ALL_BUTTON_WIDTH;
                int buttonY = topPos;
                if (mouseX >= buttonX && mouseX < buttonX + SHOW_ALL_BUTTON_WIDTH &&
                    mouseY >= buttonY && mouseY < buttonY + SHOW_ALL_BUTTON_HEIGHT) {
                    showAllSet = !showAllSet;
                    equipmentSetManager.initButtons(getShowSet());
                    return true;
                }
            }

            // 检查滚动条点击
            int scrollBarX = leftPos + 108;
            int scrollBarY = topPos + 8;
            if (mouseX >= scrollBarX && mouseX < scrollBarX + scrollUI.getScrollBarWidth() &&
                mouseY >= scrollBarY && mouseY < scrollBarY + scrollUI.getScrollAreaHeight()) {
                scrollUI.setScrolling(true);
                return true;
            }

            // 委托到SetButtonUI处理点击事件
            if (equipmentSetManager.mouseClicked(mouseX, mouseY, button)) {
                return true;
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