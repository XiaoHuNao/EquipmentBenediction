package com.xiaohunao.equipment_benediction.client.gui.widget;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import com.xiaohunao.equipment_benediction.EquipmentBenediction;
import com.xiaohunao.equipment_benediction.api.manager.EquipmentSetManager;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSet;
import com.xiaohunao.equipment_benediction.common.utils.WidgetRenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.xiaohunao.equipment_benediction.client.gui.widget.SetSwitcherTable.SetSwitcherTableSlot.*;

public class SetSwitcherTable extends AbstractWidget {
    private static final ResourceLocation FOOTER_TEXTURE = EquipmentBenediction.asResource("textures/gui/footer_background.png");
    private static final ResourceLocation PREVIOUS_PAGE_BUTTON_TEXTURE = EquipmentBenediction.asResource("textures/gui/previous_page_button.png");
    private static final ResourceLocation NEXT_PAGE_BUTTON_TEXTURE = EquipmentBenediction.asResource("textures/gui/next_page_button.png");

    private static final int MAX_COLUMNS = 4;
    private static final int FOOTER_HEIGHT = 9;
    private static final int FOOTER_WIDTH = MAX_COLUMNS * SLOT_SIZE;
    private static final int BUTTON_SIZE = 9;


    private EquipmentSet selectedEquipmentSet;
    private int currentPage = 0;

    private final LinkedList<EquipmentSet> equipmentSets = new LinkedList<>();
    private final Map<EquipmentSet, Ingredient> equipmentSetIconMap = Maps.newHashMap();
    private final BiMap<EquipmentSet, SetSwitcherTableSlot> setButtonTable = HashBiMap.create();
    
    // 页脚翻页按钮
    private final SpriteIconButton previousPageButton;
    private final SpriteIconButton nextPageButton;
    
    // 占位符槽位列表，用于补齐当前行的空槽位
    private final List<SetSwitcherTableSlot> placeholderSlots = new ArrayList<>();

    public SetSwitcherTable(int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());
        EquipmentSetManager equipmentSetManager = EquipmentSetManager.getInstance();

        for (EquipmentSet equipmentSet : equipmentSetManager.getAllResources().values()) {
            equipmentSets.add(equipmentSet);
            equipmentSetIconMap.put(equipmentSet, equipmentSet.getIconIngredient());
            setButtonTable.put(equipmentSet, new SetSwitcherTableSlot(this, equipmentSet));
        }

        if (!equipmentSets.isEmpty()) {
            selectedEquipmentSet = equipmentSets.getFirst();
            // 设置初始选中状态
            SetSwitcherTableSlot initialSlot = setButtonTable.get(selectedEquipmentSet);
            if (initialSlot != null) {
                initialSlot.setSelected(true);
            }
        }
        
        // 初始化翻页按钮
        int footerY = y + (MAX_COLUMNS * SLOT_SIZE);
        int leftButtonX = x + 2; // 左边距2像素
        int rightButtonX = x + FOOTER_WIDTH - BUTTON_SIZE - 2; // 右边距2像素
        int buttonY = footerY + (FOOTER_HEIGHT - BUTTON_SIZE) / 2;
        
        this.previousPageButton = new SpriteIconButton.Builder(button -> previousPage())
                .bounds(leftButtonX, buttonY, BUTTON_SIZE, BUTTON_SIZE)
                .textureSize(BUTTON_SIZE, BUTTON_SIZE * 3)
                .sprite(PREVIOUS_PAGE_BUTTON_TEXTURE)
                .build();
                
        this.nextPageButton = new SpriteIconButton.Builder(button -> nextPage())
                .bounds(rightButtonX, buttonY, BUTTON_SIZE, BUTTON_SIZE)
                .textureSize(BUTTON_SIZE, BUTTON_SIZE * 3)
                .sprite(NEXT_PAGE_BUTTON_TEXTURE)
                .build();
        
        // 初始化占位符槽位（最多需要MAX_COLUMNS个占位符）
        for (int i = 0; i < MAX_COLUMNS; i++) {
            SetSwitcherTableSlot placeholderSlot = new SetSwitcherTableSlot(this, null);
            placeholderSlot.active = false;
            placeholderSlots.add(placeholderSlot);
        }
    }

    public EquipmentSet getSelectedEquipmentSet() {
        return selectedEquipmentSet;
    }

    /**
     * 设置选中的装备套装
     */
    public void setSelectedEquipmentSet(EquipmentSet equipmentSet) {
        // 先清除所有槽位的选中状态
        setButtonTable.values().forEach(slot -> slot.setSelected(false));

        // 设置新的选中装备套装
        this.selectedEquipmentSet = equipmentSet;

        // 设置对应槽位的选中状态
        SetSwitcherTableSlot slot = setButtonTable.get(equipmentSet);
        if (slot != null) {
            slot.setSelected(true);
        }
    }

    /**
     * 更新所有槽位的选中状态 在切换页面后调用，确保选中状态正确显示
     */
    private void updateSelectionStates() {
        setButtonTable.forEach((equipmentSet, slot) -> {
            slot.setSelected(equipmentSet.equals(selectedEquipmentSet));
        });
    }

    /**
     * 切换到下一页
     */
    public void nextPage() {
        int total = equipmentSets.size();
        int pageCapacity = MAX_COLUMNS * MAX_COLUMNS; // 4*4 = 16个物品
        int totalPages = Math.max(1, (int) Math.ceil(total / (float) pageCapacity));

        if (currentPage < totalPages - 1) {
            currentPage++;
            updateSelectionStates(); // 更新选中状态
        }
    }

    /**
     * 切换到上一页
     */
    public void previousPage() {
        if (currentPage > 0) {
            currentPage--;
            updateSelectionStates(); // 更新选中状态
        }
    }

    /**
     * 获取当前页面
     */
    public int getCurrentPage() {
        return currentPage;
    }

    /**
     * 获取总页数
     */
    public int getTotalPages() {
        int total = equipmentSets.size();
        int pageCapacity = MAX_COLUMNS * MAX_COLUMNS; // 4*4 = 16个物品
        return Math.max(1, (int) Math.ceil(total / (float) pageCapacity));
    }

    /**
     * 渲染边框，包裹整个表格和页脚
     */
    private void renderBorder(GuiGraphics guiGraphics) {
        // 计算边框的尺寸
        int totalHeight = MAX_COLUMNS * SLOT_SIZE + FOOTER_HEIGHT;
        int totalWidth =  FOOTER_WIDTH;
        
        // 使用 SetSwitcherTableSlot 中的边框参数
        WidgetRenderUtils.drawNinePatchFrameForContent(
                guiGraphics,getX(),getY(),totalWidth,totalHeight,SWITCHER_SLOT,
                0,0,SLOT_CORNER_SIZE,SLOT_EDGE_THICKNESS,SLOT_SIZE,SEP,
                TEXTURE_WIDTH,TEXTURE_HEIGHT,SLOT_CORNER_SIZE,SLOT_EDGE_THICKNESS
        );
    }

    /**
     * 渲染页脚，包括背景贴图、左右翻页按钮和中间页数文本
     */
    private void renderFooter(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        int footerY = getY() + (MAX_COLUMNS * SLOT_SIZE);
        
        // 渲染页脚背景
        guiGraphics.blit(FOOTER_TEXTURE, 
                getX(), footerY, 
                0, 0, 
                FOOTER_WIDTH, FOOTER_HEIGHT, 
                FOOTER_WIDTH, FOOTER_HEIGHT);

        // 获取字体和页数信息
        Minecraft minecraft = net.minecraft.client.Minecraft.getInstance();
        Font font = minecraft.font;
        int totalPages = getTotalPages();
        String pageText = (currentPage + 1) + "/" + totalPages;
        
        // 计算文本位置（居中）
        int pageTextWidth = font.width(pageText);
        int centerX = getX() + (FOOTER_WIDTH - pageTextWidth) / 2;
        int textY = footerY + (FOOTER_HEIGHT - font.lineHeight) / 2 + 1;
        
        // 渲染页数文本
        guiGraphics.drawString(font, pageText, centerX, textY, 0xFFEEEEEE);
        
        // 渲染左右翻页按钮
        renderPageButtons(guiGraphics, font, footerY, mouseX, mouseY);
    }

    /**
     * 渲染占位符槽位（补齐当前行的空槽位）
     */
    private void renderPlaceholderSlots(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, int contentCount, int cols) {
        // 计算当前行已使用的槽位数量
        int currentRowSlots = contentCount % cols;
        if (currentRowSlots == 0 && contentCount > 0) {
            // 如果当前行已满，不需要占位符
            return;
        }

        // 计算当前行号
        int currentRow = contentCount / cols;

        // 计算需要多少个占位符
        int placeholderCount = cols - currentRowSlots;

        // 渲染占位符槽位
        for (int i = 0; i < placeholderCount; i++) {
            SetSwitcherTableSlot placeholderSlot = placeholderSlots.get(i);
            int col = currentRowSlots + i;
            int x = getX() + col * SLOT_SIZE;
            int y = getY() + currentRow * SLOT_SIZE;

            placeholderSlot.setPosition(x, y);
            placeholderSlot.shouldRender = true;

            // 渲染占位符槽位（只渲染背景，不可交互）
            placeholderSlot.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        }
    }

    /**
     * 渲染翻页按钮
     */
    private void renderPageButtons(GuiGraphics guiGraphics, net.minecraft.client.gui.Font font, int footerY, int mouseX, int mouseY) {
        int totalPages = getTotalPages();
        
        // 更新按钮可见性
        previousPageButton.visible = currentPage > 0;
        nextPageButton.visible = currentPage < totalPages - 1;
        
        // 渲染按钮
        if (previousPageButton.visible) {
            previousPageButton.render(guiGraphics, mouseX, mouseY, 0);
        }
        
        if (nextPageButton.visible) {
            nextPageButton.render(guiGraphics, mouseX, mouseY, 0);
        }
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (equipmentSets.isEmpty()) {
            return;
        }
        guiGraphics.pose().pushPose();
        // 在整个表格渲染期间，尽量避免写入深度缓冲，防止遮挡后续 tooltip
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        
        // 设置较高的Z层级，确保表格在盔甲架之上渲染；tooltip 仍在更高层
        guiGraphics.pose().translate(0, 0, 300.0F);

        // 首先渲染边框，包裹整个表格和页脚
        renderBorder(guiGraphics);

        int total = equipmentSets.size();
        int cols = MAX_COLUMNS;
        int pageCapacity = MAX_COLUMNS * MAX_COLUMNS; // 4*4 = 16个物品
        int totalPages = Math.max(1, (int) Math.ceil(total / (float) pageCapacity));
        if (currentPage >= totalPages) {
            currentPage = totalPages - 1;
        }

        // 当前页数据
        int startIndex = currentPage * pageCapacity;
        int remaining = Math.max(0, total - startIndex);
        int contentCount = Math.min(pageCapacity, remaining);

        // 第一阶段：重置所有占位符槽位为不可见
        placeholderSlots.forEach(slot -> slot.shouldRender = false);

        // 第二阶段：更新位置和可见性，并调用组件自身渲染以便处理悬停与tooltip（仅绘制背景）
        setButtonTable.forEach((equipmentSet, setSwitcherTableSlot) -> {
            int globalIndex = equipmentSets.indexOf(equipmentSet);
            if (globalIndex >= startIndex && globalIndex < startIndex + contentCount) {
                // 在当前页面范围内，计算页面内的相对位置
                int pageRelativeIndex = globalIndex - startIndex;
                int row = pageRelativeIndex / cols;
                int col = pageRelativeIndex % cols;

                // 设置位置
                setSwitcherTableSlot.setPosition(
                        getX() + col * SLOT_SIZE,
                        getY() + row * SLOT_SIZE
                );

                // 设置为可见
                setSwitcherTableSlot.shouldRender = true;
                // 调用组件渲染，内部会处理悬停状态与tooltip调度
                setSwitcherTableSlot.render(guiGraphics, mouseX, mouseY, partialTick);
            } else {
                // 不在当前页面，设置为不可见
                setSwitcherTableSlot.shouldRender = false;
            }
        });
        
        // 第三阶段：在所有槽位背景绘制完成后，统一绘制选中框，确保位于最上层
        setButtonTable.forEach((equipmentSet, setSwitcherTableSlot) -> {
            if (setSwitcherTableSlot.shouldRender) {
                setSwitcherTableSlot.renderSelection(guiGraphics, mouseX, mouseY, partialTick);
            }
        });

        // 第四阶段：渲染占位符槽位（补齐当前行的空槽位）
        renderPlaceholderSlots(guiGraphics, mouseX, mouseY, partialTick, contentCount, cols);

        // 第五阶段：渲染页脚
        renderFooter(guiGraphics, mouseX, mouseY, partialTick);

        // 恢复矩阵
        guiGraphics.pose().popPose();
        // 恢复并清理深度缓冲，确保后续 tooltip 不被此前 GUI 写入遮挡
        RenderSystem.depthMask(true);
        RenderSystem.clearDepth(1.0D);
        RenderSystem.clear(256, Minecraft.ON_OSX);
        RenderSystem.enableDepthTest();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!this.active || !this.visible) {
            return false;
        }

        // 检查鼠标是否在表格区域内
        if (mouseX < getX() || mouseX >= getX() + getWidth() ||
            mouseY < getY() || mouseY >= getY() + getHeight()) {
            return false;
        }

        // 首先检查是否点击了页脚按钮
        if (handleFooterClick(mouseX, mouseY, button)) {
            return true;
        }

        // 计算当前页面的数据
        int total = equipmentSets.size();
        int cols = MAX_COLUMNS;
        int pageCapacity = MAX_COLUMNS * MAX_COLUMNS; // 4*4 = 16个物品
        int totalPages = Math.max(1, (int) Math.ceil(total / (float) pageCapacity));
        if (currentPage >= totalPages) {
            currentPage = totalPages - 1;
        }

        int startIndex = currentPage * pageCapacity;
        int remaining = Math.max(0, total - startIndex);
        int contentCount = Math.min(pageCapacity, remaining);

        // 计算鼠标点击的slot位置
        int relativeX = (int) (mouseX - getX());
        int relativeY = (int) (mouseY - getY());

        int col = relativeX / SLOT_SIZE;
        int row = relativeY / SLOT_SIZE;

        // 检查是否在有效范围内
        if (col >= 0 && col < cols && row >= 0 && row < cols) {
            int pageRelativeIndex = row * cols + col;
            int globalIndex = startIndex + pageRelativeIndex;

            // 检查是否在有效的内容范围内
            if (pageRelativeIndex < contentCount && globalIndex < equipmentSets.size()) {
                EquipmentSet clickedSet = equipmentSets.get(globalIndex);
                SetSwitcherTableSlot slot = setButtonTable.get(clickedSet);
                if (slot != null) {
                    slot.onPress();
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 处理页脚按钮的点击事件
     */
    private boolean handleFooterClick(double mouseX, double mouseY, int button) {
        int footerY = getY() + (MAX_COLUMNS * SLOT_SIZE);
        
        // 检查是否在页脚区域内
        if (mouseY < footerY || mouseY >= footerY + FOOTER_HEIGHT) {
            return false;
        }

        // 处理左翻页按钮点击
        if (previousPageButton.visible && previousPageButton.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }

        // 处理右翻页按钮点击
        if (nextPageButton.visible && nextPageButton.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }

        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (!this.active || !this.visible) {
            return false;
        }

        // 处理按钮的鼠标释放事件
        boolean handled = false;
        if (previousPageButton.visible) {
            handled |= previousPageButton.mouseReleased(mouseX, mouseY, button);
        }
        if (nextPageButton.visible) {
            handled |= nextPageButton.mouseReleased(mouseX, mouseY, button);
        }

        return handled;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (!this.active || !this.visible) {
            return false;
        }

        // 仅当鼠标位于表格范围内时处理滚轮
        if (mouseX < getX() || mouseX >= getX() + getWidth() ||
                mouseY < getY() || mouseY >= getY() + getHeight()) {
            return false;
        }

        if (equipmentSets.isEmpty()) {
            return false;
        }

        if (scrollY == 0.0D) {
            return false;
        }

        int total = equipmentSets.size();
        int pageCapacity = MAX_COLUMNS * MAX_COLUMNS; // 4*4 = 16

        int currentIndex = equipmentSets.indexOf(selectedEquipmentSet);
        if (currentIndex < 0) {
            currentIndex = 0;
        }

        // 垂直滚动：向下(scrollY < 0) -> 下一个；向上(scrollY > 0) -> 上一个
        boolean forward = scrollY < 0;
        int newIndex = forward ? (currentIndex + 1) % total : (currentIndex - 1 + total) % total;

        setSelectedEquipmentSet(equipmentSets.get(newIndex));

        // 若新选中项不在当前页，切换到其所在页
        int newPage = newIndex / pageCapacity;
        if (newPage != currentPage) {
            currentPage = newPage;
            updateSelectionStates();
        }

        return true;
    }


    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    public static class SetSwitcherTableSlot extends Button {

        public static final ResourceLocation SWITCHER_SLOT = EquipmentBenediction.asResource("textures/gui/set_switcher_slot.png");

        //贴图尺寸
        public static final int TEXTURE_WIDTH = 56;
        public static final int TEXTURE_HEIGHT = 34;
        public static final int SLOT_TEXTURE_SIZE = 34;
        public static final int SLOT_CORNER_SIZE = 7;
        public static final int SLOT_EDGE_THICKNESS = 7;
        public static final int SLOT_SIZE = 18;
        public static final int SEP = 1;

        // 物品轮播相关
        private static long animationStartTime = 0;
        private static final long ROTATION_INTERVAL = 500; // 0.5秒轮播一次

        private final EquipmentSet equipmentSet;
        private boolean shouldRender = false; // 控制slot是否应该渲染
        private boolean isSelected = false; // 控制slot是否被选中

        protected SetSwitcherTableSlot(SetSwitcherTable table, EquipmentSet equipmentSetParam) {
            super(new Builder(Component.empty(), button -> {
                if (button instanceof SetSwitcherTableSlot slot && slot.equipmentSet != null) {
                    table.setSelectedEquipmentSet(slot.equipmentSet);
                }
            })
                    .size(SLOT_SIZE, SLOT_SIZE)
                    .tooltip(equipmentSetParam != null ?
                        Tooltip.create(Component.translatable(EquipmentBenediction.asDescriptionId("gui.set_switcher.tooltip.set_name." + equipmentSetParam.getName().toLanguageKey()))) :
                        null)
            );
            this.setTooltipDelay(Duration.ofMillis(300));
            this.equipmentSet = equipmentSetParam;
        }

        /**
         * 获取是否被选中
         */
        public boolean isSelected() {
            return isSelected;
        }

        /**
         * 设置选中状态
         */
        public void setSelected(boolean selected) {
            this.isSelected = selected;
        }

        /**
         * 获取装备套装
         */
        public EquipmentSet getEquipmentSet() {
            return equipmentSet;
        }

        /**
         * 获取当前应该显示的物品索引（基于全局动画时间）
         */
        private int getCurrentItemIndex(ItemStack[] items) {
            if (items == null || items.length == 0) {
                return 0;
            }
            if (items.length == 1) {
                return 0;
            }

            long currentTime = System.currentTimeMillis();
            if (animationStartTime == 0) {
                animationStartTime = currentTime;
            }

            long elapsed = currentTime - animationStartTime;
            return (int) (elapsed / ROTATION_INTERVAL) % items.length;
        }

        @Override
        protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            if (!shouldRender) {
                return;
            }

            renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        }

        /**
         * 渲染slot背景
         */
        public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            if (!shouldRender) {
                return;
            }

            int offset = SLOT_CORNER_SIZE + SEP;
            guiGraphics.blit(SWITCHER_SLOT, getX(), getY(), offset, offset, SLOT_SIZE, SLOT_SIZE, TEXTURE_WIDTH, TEXTURE_HEIGHT);
            
            // 渲染套装效果对应的物品图标
            renderItemIcon(guiGraphics);
        }

        /**
         * 渲染物品图标
         */
        private void renderItemIcon(GuiGraphics guiGraphics) {
            if (equipmentSet == null) {
                return;
            }

            Ingredient ingredient = equipmentSet.getIconIngredient();
            if (ingredient == null) {
                return;
            }

            ItemStack[] items = ingredient.getItems();
            if (items == null || items.length == 0) {
                return;
            }

            int currentIndex = getCurrentItemIndex(items);
            ItemStack currentItem = items[currentIndex];

            if (currentItem.isEmpty()) {
                return;
            }

            // 在18x18的格子中居中渲染16x16的物品图标
            int itemSize = 16;
            int offsetX = (SLOT_SIZE - itemSize) / 2;
            int offsetY = (SLOT_SIZE - itemSize) / 2;

            // 禁用深度测试，避免物品图标写入/比较深度而遮挡后续的 tooltip
            RenderSystem.disableDepthTest();
            try {
                guiGraphics.renderItem(currentItem, getX() + offsetX, getY() + offsetY);
            } finally {
                RenderSystem.enableDepthTest();
            }
        }

        /**
         * 渲染选中状态
         */
        public void renderSelection(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            if (!shouldRender || !isSelected) {
                return;
            }

            guiGraphics.blit(SWITCHER_SLOT, getX() - 2, getY() - 4, 34, 0, 22, 26, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        }


        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (!shouldRender || !this.active || !this.visible || equipmentSet == null) {
                return false;
            }

            // 检查鼠标是否在按钮区域内
            if (mouseX >= getX() && mouseX < getX() + getWidth()
                    && mouseY >= getY() && mouseY < getY() + getHeight()
                    && button == 0) { // 左键点击
                this.onPress();
                return true;
            }
            return false;
        }
    }
}