package com.xiaohunao.equipment_benediction.client.gui.screen;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.xiaohunao.equipment_benediction.EquipmentBenediction;
import com.xiaohunao.equipment_benediction.api.EquipmentSetManager;
import com.xiaohunao.equipment_benediction.common.attachment.EntityHookManager;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSet;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquippableGroup;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquippableSetData;
import com.xiaohunao.equipment_benediction.common.equippable.IEquippable;
import com.xiaohunao.equipment_benediction.common.equippable.VanillaEquippable;
import com.xiaohunao.equipment_benediction.common.init.EBAttachments;
import com.xiaohunao.equipment_benediction.client.gui.widget.EquippableSetButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.util.Mth;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.item.ItemStack;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;

public class EquipmentSetSwitcherScreen extends Screen {
    private static final ResourceLocation EQUIPMENT_SET_SWITCHER = EquipmentBenediction.asResource("textures/gui/set_switcher/equipment_set_switcher.png");
    private static final ResourceLocation EXTRA_SLOTS = EquipmentBenediction.asResource("textures/gui/set_switcher/extra_slots.png");
    private static final ResourceLocation SCROLL_BAR = EquipmentBenediction.asResource("textures/gui/set_switcher/scroll_bar.png");
    private static final ResourceLocation SCROLL_BAR_SELECTED = EquipmentBenediction.asResource("textures/gui/set_switcher/scroll_bar_selected.png");
    private static final ResourceLocation SET_SEPARATOR_BAR = EquipmentBenediction.asResource("textures/gui/set_switcher/set_separator_bar.png");
    private static final ResourceLocation SET_SHOW_ALL_CLOSE = EquipmentBenediction.asResource("textures/gui/set_switcher/set_show_all_close.png");
    private static final ResourceLocation SET_SHOW_ALL_OPEN = EquipmentBenediction.asResource("textures/gui/set_switcher/set_show_all_open.png");
    private static final ResourceLocation SET_SHOW_ALL_CLOSE_HOVERED = EquipmentBenediction.asResource("textures/gui/set_switcher/set_show_all_close_hovered.png");
    private static final ResourceLocation SET_SHOW_ALL_OPEN_HOVERED = EquipmentBenediction.asResource("textures/gui/set_switcher/set_show_all_open_hovered.png");
    private static final EquipmentSetManager setManager = EquipmentSetManager.getInstance();

    private boolean showAllSet = true;
    private final int imageWidth = 176;
    private final int imageHeight = 86;

    private final int setButtonWidth = 92;
    private final int setButtonHeight = 18;


    private int leftPos;
    private int topPos;
    private final Player player;
    private ArmorStand previewArmorStand;
    private List<EquippableSetButton> setButtons = new ArrayList<>();

    private float scrollOffs;
    private boolean scrolling;
    private final int scrollBarWidth = 12;
    private final int scrollBarHeight = 15;
    private final int scrollAreaHeight = 70;  // 可滚动区域的高度
    private final int contentAreaX = 8;  // 内容区域的X坐标
    private final int contentAreaY = 8;  // 内容区域的Y坐标
    private final int contentAreaWidth = 92;  // 内容区域的宽度
    private final int contentAreaHeight = 70;  // 内容区域的高度

    // 修改盔甲架渲染区域的常量
    private final int armorStandPreviewX1 = 120;  // 预览区域左边界
    private final int armorStandPreviewX2 = 170;  // 预览区域右边界
    private final int armorStandPreviewY1 = 10;   // 预览区域上边界
    private final int armorStandPreviewY2 = 80;   // 预览区域下边界
    private final int armorStandScale = 20;       // 减小盔甲架缩放
    private final float armorStandYOffset = -0.2F; // 调整Y轴偏移，使盔甲架上移

    private long lastRotationTime = 0;
    private static final int ROTATION_INTERVAL = 1000; // 轮播间隔，单位毫秒
    private Map<EquippableSetData, List<Map<EquipmentSlot, ItemStack>>> previewCombinations = new HashMap<>();

    public EquipmentSetSwitcherScreen(Player player) {
        super(Component.translatable("screen.equipment_benediction.equipment_set_switcher"));
        this.player = player;
    }

    @Override
    protected void init() {
        super.init();
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;
        
        // 初始化按钮列表
        initButtons();

        if (this.minecraft != null && this.minecraft.level != null) {
            Level level = this.minecraft.level;
            this.previewArmorStand = new ArmorStand(level, 0, 0, 0);
            this.previewArmorStand.setShowArms(true);
            this.previewArmorStand.setNoBasePlate(true);
            
            // 复制玩家的装备到盔甲架上
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                this.previewArmorStand.setItemSlot(slot, this.player.getItemBySlot(slot).copy());
            }
        }
    }

    private void initButtons() {
        setButtons.clear();
        Set<EquipmentSet> showSet = getShowSet();
        
        int buttonX = this.leftPos + contentAreaX;
        int buttonY = this.topPos + contentAreaY;
        int buttonSpacing = 1;
        int titleHeight = 10;

        for (EquipmentSet equipmentSet : showSet) {
            buttonY += 2; // 顶部分隔符高度
            buttonY += titleHeight; // 标题高度

            EquippableGroup equippableGroup = equipmentSet.getEquippableGroup();
            Set<EquippableSetData> equippableSets = equippableGroup.getEquippableSets();
            for (EquippableSetData setData : equippableSets) {
                boolean isExclusive = equippableGroup.isExclusive(setData);
                EquippableSetButton button = new EquippableSetButton(
                    buttonX, buttonY, setButtonWidth, setButtonHeight, setData, equipmentSet, isExclusive,
                    (btn) -> {
                        handleSetButtonClick((EquippableSetButton) btn);
                    }
                );
                setButtons.add(button);
                buttonY += setButtonHeight + buttonSpacing;
            }
            buttonY += 4; // 组间距
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBlurredBackground(partialTick);
        guiGraphics.blit(EQUIPMENT_SET_SWITCHER, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        
        // 渲染内容
        enableScissor(guiGraphics);
        this.renderEquipmentSets(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.disableScissor();
        
        // 渲染滚动条
        renderScrollBar(guiGraphics);

        // 渲染盔甲架
        renderArmorStand(guiGraphics, mouseX, mouseY, partialTick);
    }

    private void enableScissor(GuiGraphics guiGraphics) {
        int x = this.leftPos + contentAreaX;
        int y = this.topPos + contentAreaY;
        guiGraphics.enableScissor(x, y, x + contentAreaWidth, y + contentAreaHeight);
    }

    private void renderScrollBar(GuiGraphics guiGraphics) {
        int scrollBarX = this.leftPos + 103;  // 滚动条X坐标
        int scrollBarY = this.topPos + 8;     // 滚动条Y坐标
        
        // 计算滚动条位置
        int maxScroll = getMaxScroll();
        if (maxScroll > 0) {
            int scrollBarPosition = (int) (scrollBarY + scrollOffs * (scrollAreaHeight - scrollBarHeight));
            ResourceLocation scrollTexture = scrolling ? SCROLL_BAR_SELECTED : SCROLL_BAR;
            guiGraphics.blit(scrollTexture, scrollBarX, scrollBarPosition, 0, 0, scrollBarWidth, scrollBarHeight,scrollBarWidth, scrollBarHeight);
        }
    }

    private void renderEquipmentSets(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        Set<EquipmentSet> showSet = getShowSet();
        
        int buttonX = this.leftPos + contentAreaX;
        int buttonY = this.topPos + contentAreaY - (int)(getMaxScroll() * scrollOffs);
        int buttonSpacing = 1;
        int titleHeight = 10;

        for (EquipmentSet equipmentSet : showSet) {
            // 渲染套装名称
            if (buttonY >= this.topPos + contentAreaY && 
                buttonY <= this.topPos + contentAreaY + contentAreaHeight) {
                ResourceLocation resource = setManager.getResource(equipmentSet);
                String translationKey = resource.getNamespace() + "." + resource.getPath();
                Component name = Component.translatable("equipment_benediction.set_switcher." + translationKey);
                
                int textWidth = minecraft.font.width(name);
                int textX = buttonX + (setButtonWidth - textWidth) / 2;
                
                guiGraphics.drawString(minecraft.font, name, textX, buttonY, 0x808080);
            }
            buttonY += titleHeight;

            // 更新并渲染按钮
            Set<EquippableSetData> equippableSets = equipmentSet.getEquippableGroup().getEquippableSets();
            for (EquippableSetData setData : equippableSets) {
                if (buttonY + setButtonHeight >= this.topPos + contentAreaY && 
                    buttonY <= this.topPos + contentAreaY + contentAreaHeight) {
                    // 查找对应的按钮并更新位置
                    for (EquippableSetButton button : setButtons) {
                        if (button.getSetData() == setData && button.getEquipmentSet() == equipmentSet) {
                            button.setX(buttonX);
                            button.setY(buttonY);
                            button.render(guiGraphics, mouseX, mouseY, partialTick);
                            break;
                        }
                    }
                }
                buttonY += setButtonHeight + buttonSpacing;
            }

            // 渲染底部分隔符
            if (buttonY >= this.topPos + contentAreaY &&
                buttonY <= this.topPos + contentAreaY + contentAreaHeight) {
                guiGraphics.blit(SET_SEPARATOR_BAR, buttonX, buttonY, 0, 0, setButtonWidth, 2, setButtonWidth, 2);
            }
            buttonY += 4;
        }
    }

    private void handleSetButtonClick(EquippableSetButton clickedButton) {
        EquipmentSet equipmentSet = clickedButton.getEquipmentSet();
        boolean isExclusive = clickedButton.isExclusive();
        
        // 如果点击的是独占按钮
        if (isExclusive) {
            // 如果已经选中，则取消选中
            if (clickedButton.isSelected()) {
                clickedButton.setSelected(false);
            } else {
                // 如果未选中，则选中它并取消同组其他按钮的选中状态
                for (EquippableSetButton button : setButtons) {
                    if (button.getEquipmentSet() == equipmentSet) {
                        button.setSelected(false);
                    }
                }
                clickedButton.setSelected(true);
            }
        } else {
            // 如果点击的是非独占按钮
            // 检查是否有同组的独占按钮被选中
            boolean hasExclusiveSelected = false;
            for (EquippableSetButton button : setButtons) {
                if (button.getEquipmentSet() == equipmentSet && 
                    button.isExclusive() && 
                    button.isSelected()) {
                    hasExclusiveSelected = true;
                    button.setSelected(false); // 取消独占按钮的选中状态
                    break;
                }
            }
            
            // 切换当前非独占按钮的选中状态
            clickedButton.setSelected(!clickedButton.isSelected());
        }
    }

    private int getMaxScroll() {
        int totalHeight = 0;
        for (EquipmentSet equipmentSet : getShowSet()) {
            totalHeight += equipmentSet.getEquippableGroup().getEquippableSets().size() * 
                (setButtonHeight + 1) + 4 + 10;  // 按钮高度 + 间距 + 分隔符高度
        }
        return Math.max(0, totalHeight - contentAreaHeight);
    }

    private Set<EquipmentSet> getShowSet(){
        Set<EquipmentSet> equipmentSets = Sets.newHashSet();

        if (showAllSet){
            equipmentSets.addAll(setManager.getAllResources().values());
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
        this.previewArmorStand = null;  // 清理盔甲架实例
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {  // 左键点击
            // 检查滚动条点击
            int scrollBarX = this.leftPos + 108;
            int scrollBarY = this.topPos + 8;
            if (mouseX >= scrollBarX && mouseX < scrollBarX + scrollBarWidth &&
                mouseY >= scrollBarY && mouseY < scrollBarY + scrollAreaHeight) {
                this.scrolling = true;
                return true;
            }

            // 检查按钮点击
            for (EquippableSetButton button1 : setButtons) {
                if (button1.isMouseOver(mouseX, mouseY)) {
                    button1.onPress();
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            this.scrolling = false;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.scrolling && getMaxScroll() > 0) {
            int scrollBarY = this.topPos + 8;
            float dragDistance = ((float) mouseY - scrollBarY) / (float) (scrollAreaHeight - scrollBarHeight);
            this.scrollOffs = Mth.clamp(dragDistance, 0.0F, 1.0F);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (getMaxScroll() > 0) {
            float scrollAmount = 1.0F / getMaxScroll();
            this.scrollOffs = Mth.clamp(this.scrollOffs - (float)verticalAmount * scrollAmount, 0.0F, 1.0F);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    private void renderArmorStand(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (this.minecraft == null || this.previewArmorStand == null) return;

        // 清除盔甲架上的所有装备
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            this.previewArmorStand.setItemSlot(slot, ItemStack.EMPTY);
        }

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastRotationTime > ROTATION_INTERVAL) {
            // 更新预览组合
            updatePreviewCombinations();
            lastRotationTime = currentTime;
        }

        // 先处理独占的套装
        for (EquippableSetButton button : setButtons) {
            if (button.isSelected() && button.isExclusive()) {
                applySetToArmorStand(button.getSetData(), currentTime);
                // 如果是独占的，渲染完就返回
                int x1 = this.leftPos + 120;
                int y1 = this.topPos + 10;
                int x2 = this.leftPos + 170;
                int y2 = this.topPos + 80;

                float rotationAngle = (float) ((player.tickCount % 1080) * 360) / 1080;

                InventoryScreen.renderEntityInInventoryFollowsAngle(
                    guiGraphics, 
                    x1, y1, x2, y2,
                    30, 0.0F, rotationAngle,
                    0.0F,
                    this.previewArmorStand
                );
                return;
            }
        }

        // 处理非独占的套装
        for (EquippableSetButton button : setButtons) {
            if (button.isSelected() && !button.isExclusive()) {
                applySetToArmorStand(button.getSetData(), currentTime);
            }
        }

        int x1 = this.leftPos + 120;
        int y1 = this.topPos + 10;
        int x2 = this.leftPos + 170;
        int y2 = this.topPos + 80;

        float rotationAngle = (float) ((player.tickCount % 1080) * 360) / 1080;

        InventoryScreen.renderEntityInInventoryFollowsAngle(
            guiGraphics, 
            x1, y1, x2, y2,
            30, 0.0F, rotationAngle,
            0.0F,
            this.previewArmorStand
        );
    }

    // 新增辅助方法来应用套装到盔甲架
    private void applySetToArmorStand(EquippableSetData setData, long currentTime) {
        if (setData.getRequiredMatchCount() != null) {
            // 使用预先计算的轮播组合
            List<Map<EquipmentSlot, ItemStack>> combinations = previewCombinations.get(setData);
            if (combinations != null && !combinations.isEmpty()) {
                int index = (int) ((currentTime / ROTATION_INTERVAL) % combinations.size());
                Map<EquipmentSlot, ItemStack> currentPreview = combinations.get(index);
                for (Map.Entry<EquipmentSlot, ItemStack> entry : currentPreview.entrySet()) {
                    // 只在槽位为空时设置装备，避免覆盖
                    if (this.previewArmorStand.getItemBySlot(entry.getKey()).isEmpty()) {
                        this.previewArmorStand.setItemSlot(entry.getKey(), entry.getValue().copy());
                    }
                }
            }
        } else {
            // 原有的完整套装渲染逻辑
            Map<IEquippable, Ingredient> equipages = setData.equipages();
            for (Map.Entry<IEquippable, Ingredient> entry : equipages.entrySet()) {
                IEquippable equippable = entry.getKey();
                if (equippable instanceof VanillaEquippable vanillaEquippable) {
                    EquipmentSlot slot = vanillaEquippable.getSlotType();
                    // 只在槽位为空时设置装备，避免覆盖
                    if (this.previewArmorStand.getItemBySlot(slot).isEmpty()) {
                        Ingredient ingredient = entry.getValue();
                        ItemStack[] matchingStacks = ingredient.getItems();
                        if (matchingStacks.length > 0) {
                            this.previewArmorStand.setItemSlot(slot, matchingStacks[0].copy());
                        }
                    }
                }
            }
        }
    }

    // 添加新的辅助方法来更新预览组合
    private void updatePreviewCombinations() {
        previewCombinations.clear();
        for (EquippableSetButton button : setButtons) {
            if (button.isSelected()) {
                EquippableSetData setData = button.getSetData();
                if (setData.getRequiredMatchCount() != null) {
                    List<Map<EquipmentSlot, ItemStack>> combinations = generatePreviewCombinations(setData);
                    previewCombinations.put(setData, combinations);
                }
            }
        }
    }

    private List<Map<EquipmentSlot, ItemStack>> generatePreviewCombinations(EquippableSetData setData) {
        List<Map<EquipmentSlot, ItemStack>> combinations = new ArrayList<>();
        Map<IEquippable, Ingredient> equipages = setData.equipages();
        Integer requiredCount = setData.getRequiredMatchCount();
        
        if (requiredCount == null) {
            return combinations;
        }
        
        // 创建每个槽位可用物品的映射
        Map<EquipmentSlot, List<ItemStack>> slotItems = new HashMap<>();
        for (Map.Entry<IEquippable, Ingredient> entry : equipages.entrySet()) {
            if (entry.getKey() instanceof VanillaEquippable vanillaEquippable) {
                EquipmentSlot slot = vanillaEquippable.getSlotType();
                ItemStack[] items = entry.getValue().getItems();
                if (items.length > 0) {
                    slotItems.put(slot, Arrays.asList(items));
                }
            }
        }

        // 生成不同的组合
        List<EquipmentSlot> availableSlots = new ArrayList<>(slotItems.keySet());
        if (availableSlots.size() >= requiredCount) {
            generateCombinationsHelper(new ArrayList<>(), availableSlots, 0, requiredCount, slotItems, combinations);
        }

        return combinations;
    }

    private void generateCombinationsHelper(
        List<EquipmentSlot> current,
        List<EquipmentSlot> slots,
        int start,
        int remaining,
        Map<EquipmentSlot, List<ItemStack>> slotItems,
        List<Map<EquipmentSlot, ItemStack>> result
    ) {
        if (remaining == 0) {
            Map<EquipmentSlot, ItemStack> combination = new HashMap<>();
            for (EquipmentSlot slot : current) {
                List<ItemStack> items = slotItems.get(slot);
                combination.put(slot, items.get(0)); // 使用第一个可用物品
            }
            result.add(combination);
            return;
        }

        for (int i = start; i <= slots.size() - remaining; i++) {
            current.add(slots.get(i));
            generateCombinationsHelper(current, slots, i + 1, remaining - 1, slotItems, result);
            current.remove(current.size() - 1);
        }
    }
} 