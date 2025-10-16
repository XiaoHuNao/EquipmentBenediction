package com.xiaohunao.equipment_benediction.client.gui.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.xiaohunao.equipment_benediction.EquipmentBenediction;
import com.xiaohunao.equipment_benediction.api.manager.EquipmentSetManager;
import com.xiaohunao.equipment_benediction.client.gui.widget.BranchCardWidget;
import com.xiaohunao.equipment_benediction.client.gui.widget.SetSwitcherTable;
import com.xiaohunao.equipment_benediction.client.gui.widget.SpriteIconButton;
import com.xiaohunao.equipment_benediction.client.gui.widget.VerticalScrollbar;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSetBranch;
import com.xiaohunao.equipment_benediction.common.equippable.IWearable;
import com.xiaohunao.equipment_benediction.common.equippable.VanillaWearable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class NewEquipmentSetSwitcherScreen extends Screen {

    // 背景主界面贴图（装备套装切换界面）
    public static final ResourceLocation EQUIPMENT_SET_SWITCHER = EquipmentBenediction.asResource("textures/gui/equipment_set_switch.png");
    // 右侧滚动条贴图
    public static final ResourceLocation SCROLL = EquipmentBenediction.asResource("textures/gui/scroll_bar.png");
    //
    public static final ResourceLocation BUTTON = EquipmentBenediction.asResource("textures/gui/set_switcher_button.png");

    // 整个界面
    public static final int WIDTH = 220;
    public static final int HEIGHT = 156;

    // 滚动条
    private VerticalScrollbar scrollbar;

    // 装备套装选择相关
    private SpriteIconButton setSwitcherButton;

    private SetSwitcherTable setSwitcherTable;

    // 分支卡片组件
    private BranchCardWidget branchCardWidget;

    // 右侧列表内容总高度（用于滚动计算）
    private int contentHeight;

    // 基于背景贴图可视区域的布局参数
    private static final int CONTENT_OFFSET_X = 7; // 内容区相对背景的X偏移
    private static final int CONTENT_OFFSET_Y = 7; // 内容区相对背景的Y偏移
    // 左侧盔甲架预览区域：宽89，高143
    private static final int LEFT_PANE_X = CONTENT_OFFSET_X; // 左侧面板X
    private static final int LEFT_PANE_Y = CONTENT_OFFSET_Y; // 左侧面板Y
    private static final int LEFT_PANE_W = 89; // 左侧面板宽度
    private static final int LEFT_PANE_H = 143; // 左侧面板高度
    // 中间分割线宽度：5
    private static final int MIDDLE_GAP_W = 5;
    // 右侧列表区域：总宽112（包含10px滚动槽），高143
    private static final int RIGHT_PANE_X = CONTENT_OFFSET_X + LEFT_PANE_W + MIDDLE_GAP_W; // 右侧面板X
    private static final int RIGHT_PANE_Y = CONTENT_OFFSET_Y; // 右侧面板Y
    private static final int RIGHT_PANE_W = 112; // 右侧面板总宽（含滚动槽）
    private static final int RIGHT_PANE_H = 143; // 右侧面板高度

    private static final int BUTTON_OFFSET_X = 2;
    private static final int BUTTON_OFFSET_Y = 2;

    // 左侧盔甲架实体（自旋展示）
    private ArmorStand previewArmorStand;
    // 自旋角度（度）
    private float spinYawDeg = 0.0f;

    // 基础装备快照（用于在预览时作为底板）
    private final Map<EquipmentSlot, ItemStack> baseEquipment = new LinkedHashMap<>();

    // 悬停分支与组合轮播
    private EquipmentSetBranch hoveredBranch;
    private final List<Map<EquipmentSlot, ItemStack>> previewCombos = new ArrayList<>();
    private int currentComboIndex = 0;
    private long lastSwitchMillis = 0L;
    private static final long SWITCH_INTERVAL_MS = TimeUnit.SECONDS.toMillis(1);

    public NewEquipmentSetSwitcherScreen() {
        super(Component.translatable("screen.equipment_benediction.equipment_set_switcher"));
    }

    @Override
    protected void init() {
        EquipmentSetManager equipmentSetManager = EquipmentSetManager.getInstance();

        int x = (this.width - WIDTH) / 2;
        int y = (this.height - HEIGHT) / 2;
        int viewX = x + RIGHT_PANE_X;
        int viewY = y + RIGHT_PANE_Y;
        int viewW = RIGHT_PANE_W - 8;
        int viewH = RIGHT_PANE_H;

        this.scrollbar = new VerticalScrollbar(viewX + viewW, viewY, viewH, 8, SCROLL);

        this.setSwitcherButton = new SpriteIconButton.Builder()
                .bounds(x + LEFT_PANE_X + BUTTON_OFFSET_X, y + LEFT_PANE_Y + BUTTON_OFFSET_Y, 18, 18)
                .sprite(BUTTON)
                .build();

        int tableY = y + LEFT_PANE_Y + BUTTON_OFFSET_Y + setSwitcherButton.getHeight() + SetSwitcherTable.SetSwitcherTableSlot.SLOT_EDGE_THICKNESS;
        int tablex = x + LEFT_PANE_X + BUTTON_OFFSET_X + SetSwitcherTable.SetSwitcherTableSlot.SLOT_EDGE_THICKNESS;
        int tableSize = equipmentSetManager.getAllResources().size() * 18 + 7 * 2;
        this.setSwitcherTable = new SetSwitcherTable(tablex, tableY, tableSize, tableSize);

        this.branchCardWidget = new BranchCardWidget(viewX + 2, viewY + 2);

        initArmorStand();
    }

    private void initArmorStand() {
        Minecraft mc = this.minecraft;
        if (mc == null || mc.player == null) {
            return;
        }
        var player = mc.player;
        if (player == null) {
            return;
        }
        var level = player.level();
        if (level == null) {
            return;
        }

        this.previewArmorStand = new ArmorStand(level, 0, 0, 0);
        this.previewArmorStand.setShowArms(true);
        this.previewArmorStand.setNoBasePlate(true);

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack copy = player.getItemBySlot(slot).copy();
            this.previewArmorStand.setItemSlot(slot, copy);
            this.baseEquipment.put(slot, copy.copy());
        }
    }

    // 在GUI中渲染并持续自旋的实体（盔甲架）
    private void renderSpinningEntity(GuiGraphics guiGraphics, int x, int y, int size, ArmorStand armorStand, float partialTick) {
        Minecraft mc = Minecraft.getInstance();
        EntityRenderDispatcher dispatcher = mc.getEntityRenderDispatcher();
        MultiBufferSource.BufferSource buffer = mc.renderBuffers().bufferSource();
        PoseStack poseStack = guiGraphics.pose();
        armorStand.setShowArms(true);

        // 自旋：以固定角速度旋转（使用传入partialTick估算）
        spinYawDeg = (spinYawDeg + (partialTick * 3.0f)) % 360.0f;

        poseStack.pushPose();
        poseStack.translate(x, y, 200.0F);
        // 使用正向缩放，避免模型上下颠倒
        poseStack.scale(size, size, size);

        // 将模型移动到合适的深度并旋转
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0f));
        poseStack.mulPose(Axis.YP.rotationDegrees(spinYawDeg));

        // 渲染
        float oldBodyRot = armorStand.yBodyRot;
        float oldYRot = armorStand.getYRot();
        float oldXRot = armorStand.getXRot();
        armorStand.yBodyRot = 0.0f;
        armorStand.setYRot(0.0f);
        armorStand.setXRot(0.0f);

        dispatcher.setRenderShadow(false);
        // 去除额外的向下偏移，防止模型只剩头部可见
        dispatcher.render(armorStand, 0.0, 0.0, 0.0, 0.0f, partialTick, poseStack, buffer, 0x00F000F0);
        buffer.endBatch();
        dispatcher.setRenderShadow(true);

        // 还原实体姿态
        armorStand.yBodyRot = oldBodyRot;
        armorStand.setYRot(oldYRot);
        armorStand.setXRot(oldXRot);

        poseStack.popPose();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);

        int x = (this.width - WIDTH) / 2;
        int y = (this.height - HEIGHT) / 2;

        guiGraphics.blit(EQUIPMENT_SET_SWITCHER, x, y, 0, 0, 0, WIDTH, HEIGHT, 256, 256);

        // 左右两块裁剪区域
        int leftX = x + LEFT_PANE_X;
        int leftY = y + LEFT_PANE_Y;
        int leftW = LEFT_PANE_W;
        int leftH = LEFT_PANE_H;

        int viewX = x + RIGHT_PANE_X;
        int viewY = y + RIGHT_PANE_Y;
        // 右侧可视宽度：为让6px滚动条在10px槽内居中，保留8px（2px内边距+6px轨道）
        int viewW = RIGHT_PANE_W - 8; // 给滚动条与2px内边距留下空间
        int viewH = RIGHT_PANE_H;

        // 左侧：盔甲架预览
        guiGraphics.enableScissor(leftX, leftY, leftX + leftW, leftY + leftH);
//        // 测试用：左区域半透明红色背景
//        guiGraphics.fill(leftX, leftY, leftX + leftW, leftY + leftH, 0x40FF0000);
        // 根据右侧悬浮状态映射装备到盔甲架
        updateArmorStandPreview(mouseX, mouseY);
        if (previewArmorStand != null) {
            int centerX = leftX + leftW / 2;
            int centerY = leftY + leftH - (leftY / 2);
            int size = 48;
            renderSpinningEntity(guiGraphics, centerX, centerY, size, previewArmorStand, partialTick);
        }
        guiGraphics.disableScissor();

        // 渲染装备套装选择器按钮（在盔甲架之后）
        if (setSwitcherButton != null) {
            setSwitcherButton.render(guiGraphics, mouseX, mouseY, partialTick);
        }

        // 渲染装备切换器表格（在盔甲架和按钮之后，确保在最上层）
        if (setSwitcherTable != null && setSwitcherButton != null && setSwitcherButton.isSelected()) {
            setSwitcherTable.render(guiGraphics, mouseX, mouseY, partialTick);
        }

        // 右侧：分支卡片列表
        guiGraphics.enableScissor(viewX, viewY, viewX + viewW, viewY + viewH);
//        // 测试用：右区域半透明蓝色背景
//        guiGraphics.fill(viewX, viewY, viewX + viewW, viewY + viewH, 0x400000FF);

        // 渲染分支卡片组件
        if (branchCardWidget != null) {
            if (setSwitcherTable != null) {
                branchCardWidget.setEquipmentSet(setSwitcherTable.getSelectedEquipmentSet());
            }
            if (scrollbar != null) {
                branchCardWidget.setScroll01(scrollbar.getScroll01());
            }
            branchCardWidget.render(guiGraphics, mouseX, mouseY, partialTick);
        }

        guiGraphics.disableScissor();

        // 滚动条
        if (scrollbar != null) {
            // 根据当前布局更新滚动条位置（窗口缩放时生效），保留滚动值
            int viewX2 = x + RIGHT_PANE_X;
            int viewY2 = y + RIGHT_PANE_Y;
            int viewW2 = RIGHT_PANE_W - 8;
            int viewH2 = RIGHT_PANE_H;
            int scrollbarX2 = viewX2 + viewW2;
            this.scrollbar.setBounds(scrollbarX2, viewY2, viewH2, 8);
            scrollbar.render(guiGraphics, mouseX, mouseY);
        }

    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // 处理装备套装选择器按钮的点击
        if (setSwitcherButton != null && setSwitcherButton.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }

        // 处理装备套装切换表格的点击
        if (setSwitcherTable != null && setSwitcherButton != null && setSwitcherButton.isSelected()
                && setSwitcherTable.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }

        if (scrollbar != null && scrollbar.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }

        // 处理分支卡片组件的点击
        if (branchCardWidget != null && branchCardWidget.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        // 处理装备套装选择器按钮的释放
        if (setSwitcherButton != null) {
            setSwitcherButton.mouseReleased(mouseX, mouseY, button);
        }

        // 处理装备套装切换表格的释放
        if (setSwitcherTable != null && setSwitcherButton != null && setSwitcherButton.isSelected()) {
            setSwitcherTable.mouseReleased(mouseX, mouseY, button);
        }

        if (scrollbar != null) {
            scrollbar.mouseReleased(mouseX, mouseY, button);
        }

        // 处理分支卡片组件的释放
        if (branchCardWidget != null) {
            branchCardWidget.mouseReleased(mouseX, mouseY, button);
        }

        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (scrollbar != null && scrollbar.mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
            return true;
        }

        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        int x = (this.width - WIDTH) / 2;
        int y = (this.height - HEIGHT) / 2;

        // 检查是否在右侧面板区域（用于滚动条）
        int viewX = x + RIGHT_PANE_X;
        int viewY = y + RIGHT_PANE_Y;
        int viewH = RIGHT_PANE_H;
        boolean inRightPane = mouseX >= viewX && mouseX < viewX + RIGHT_PANE_W && mouseY >= viewY && mouseY < viewY + viewH;

        // 如果鼠标在右侧面板区域，处理滚动条
        if (scrollbar != null && inRightPane) {
            scrollbar.scrollByDelta(-verticalAmount * 12, contentHeight, viewH);
            return true;
        }

        // 检查是否在左侧套装切换表格区域（仅当表格展开且鼠标在表格范围内时）
        if (setSwitcherTable != null && setSwitcherButton != null && setSwitcherButton.isSelected()) {
            int tableX = setSwitcherTable.getX();
            int tableY = setSwitcherTable.getY();
            int tableW = setSwitcherTable.getWidth();
            int tableH = setSwitcherTable.getHeight();
            boolean inTable = mouseX >= tableX && mouseX < tableX + tableW && mouseY >= tableY && mouseY < tableY + tableH;
            if (inTable) {
                if (setSwitcherTable.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) {
                    return true;
                }
            }
        }

        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    // ================= 盔甲架预览映射与轮播 =================
    private void updateArmorStandPreview(int mouseX, int mouseY) {
        if (branchCardWidget == null || previewArmorStand == null) {
            return;
        }
        EquipmentSetBranch nowHovered = branchCardWidget.getHoveredBranch(mouseX, mouseY);
        if (!Objects.equals(nowHovered, hoveredBranch)) {
            hoveredBranch = nowHovered;
            rebuildPreviewCombos();
            currentComboIndex = 0;
            lastSwitchMillis = 0L;
            applyCurrentCombo();
            return;
        }

        if (hoveredBranch == null) {
            // 恢复基础装备
            applyEquipmentMap(baseEquipment);
            return;
        }

        if (previewCombos.size() > 1) {
            long now = System.currentTimeMillis();
            if (now - lastSwitchMillis >= SWITCH_INTERVAL_MS) {
                currentComboIndex = (currentComboIndex + 1) % previewCombos.size();
                applyCurrentCombo();
                lastSwitchMillis = now;
            }
        } else {
            applyCurrentCombo();
        }
    }

    private void rebuildPreviewCombos() {
        previewCombos.clear();
        if (hoveredBranch == null) {
            return;
        }

        // 收集所有可映射到盔甲架的候选（仅原版槽位）
        List<Map.Entry<IWearable, Ingredient>> entries = new ArrayList<>(hoveredBranch.equipages().entrySet());
        List<Map.Entry<VanillaWearable, ItemStack>> vanillaCandidates = new ArrayList<>();
        for (Map.Entry<IWearable, Ingredient> e : entries) {
            if (e.getKey() instanceof VanillaWearable vw) {
                ItemStack stack = firstNonEmpty(e.getValue());
                if (!stack.isEmpty()) {
                    vanillaCandidates.add(Map.entry(vw, stack));
                }
            }
        }

        Optional<Integer> matchOpt = hoveredBranch.requiredMatchCount();
        if (matchOpt.isPresent()) {
            int m = Math.min(matchOpt.get(), vanillaCandidates.size());
            if (m <= 0) {
                return;
            }
            // 生成所有大小为 m 的组合
            int n = vanillaCandidates.size();
            int[] idx = new int[m];
            for (int i = 0; i < m; i++) {
                idx[i] = i;
            }
            while (true) {
                LinkedHashMap<EquipmentSlot, ItemStack> combo = new LinkedHashMap<>();
                for (int i = 0; i < m; i++) {
                    Map.Entry<VanillaWearable, ItemStack> ent = vanillaCandidates.get(idx[i]);
                    combo.put(ent.getKey().slotType(), ent.getValue());
                }
                previewCombos.add(combo);

                int p = m - 1;
                while (p >= 0 && idx[p] == n - m + p) {
                    p--;
                }
                if (p < 0) {
                    break;
                }
                idx[p]++;
                for (int j = p + 1; j < m; j++) {
                    idx[j] = idx[j - 1] + 1;
                }
            }
        } else {
            // 单一组合：全部映射
            LinkedHashMap<EquipmentSlot, ItemStack> combo = new LinkedHashMap<>();
            for (Map.Entry<VanillaWearable, ItemStack> ent : vanillaCandidates) {
                combo.put(ent.getKey().slotType(), ent.getValue());
            }
            if (!combo.isEmpty()) {
                previewCombos.add(combo);
            }
        }
    }

    private ItemStack firstNonEmpty(Ingredient ingredient) {
        ItemStack[] items = ingredient.getItems();
        for (ItemStack s : items) {
            if (s != null && !s.isEmpty()) {
                return s.copy();
            }
        }
        return ItemStack.EMPTY;
    }

    private void applyCurrentCombo() {
        if (previewArmorStand == null) {
            return;
        }
        if (hoveredBranch == null || previewCombos.isEmpty()) {
            applyEquipmentMap(baseEquipment);
            return;
        }
        Map<EquipmentSlot, ItemStack> combo = previewCombos.get(Math.max(0, Math.min(currentComboIndex, previewCombos.size() - 1)));
        // 只映射分支组合自身（不叠加基础装备）
        applyEquipmentMap(combo);
    }

    private void applyEquipmentMap(Map<EquipmentSlot, ItemStack> map) {
        if (previewArmorStand == null) {
            return;
        }
        // 先清空所有槽位
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            previewArmorStand.setItemSlot(slot, ItemStack.EMPTY);
        }
        for (Map.Entry<EquipmentSlot, ItemStack> e : map.entrySet()) {
            previewArmorStand.setItemSlot(e.getKey(), e.getValue().copy());
        }
    }
}
