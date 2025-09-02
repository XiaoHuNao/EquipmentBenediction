package com.xiaohunao.equipment_benediction.client.gui.screen.switcher;

import com.xiaohunao.equipment_benediction.api.manager.EquipmentSetManager;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSet;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSetBranch;
import com.xiaohunao.equipment_benediction.common.equippable.IWearable;
import com.xiaohunao.equipment_benediction.common.equippable.VanillaWearable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.*;

public class ArmorStandPreviewUI {
    private static final int ROTATION_INTERVAL = 1000;
    private final int previewX1;
    private final int previewX2;
    private final int previewY1;
    private final int previewY2;
    
    private final ArmorStand previewArmorStand;
    private final Player player;
    private long lastRotationTime = 0;
    private Map<EquipmentSetBranch, List<Map<EquipmentSlot, ItemStack>>> previewCombinations = new HashMap<>();

    public ArmorStandPreviewUI(Player player, ArmorStand armorStand, int x1, int y1, int x2, int y2) {
        this.player = player;
        this.previewArmorStand = armorStand;
        this.previewX1 = x1;
        this.previewY1 = y1;
        this.previewX2 = x2;
        this.previewY2 = y2;
    }

    public void render(GuiGraphics guiGraphics, Set<EquipmentSetBranch> selectedSets) {
        if (previewArmorStand == null) return;

        clearArmorStandEquipment();
        updatePreviewIfNeeded(selectedSets);
        applySelectedSetsToArmorStand(selectedSets);
        renderArmorStand(guiGraphics);
    }

    private void clearArmorStandEquipment() {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            previewArmorStand.setItemSlot(slot, ItemStack.EMPTY);
        }
    }

    private void updatePreviewIfNeeded(Set<EquipmentSetBranch> selectedSets) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastRotationTime > ROTATION_INTERVAL) {
            updatePreviewCombinations(selectedSets);
            lastRotationTime = currentTime;
        }
    }

    private void renderArmorStand(GuiGraphics guiGraphics) {
        float rotationAngle = (float) ((player.tickCount % 1080) * 360) / 1080;
        InventoryScreen.renderEntityInInventoryFollowsAngle(
            guiGraphics,
            previewX1, previewY1, previewX2, previewY2,
            30, 0.0F, rotationAngle, 0.0F,
            previewArmorStand
        );
    }

    private void applySelectedSetsToArmorStand(Set<EquipmentSetBranch> selectedSets) {
        // 先处理独占的套装
        for (EquipmentSetBranch setData : selectedSets) {
            EquipmentSet equipmentSet = findEquipmentSetForData(setData);
            if (equipmentSet != null && equipmentSet.getEquippableGroup().isExclusive(setData)) {
                applySetToArmorStand(setData);
                return;
            }
        }

        // 处理非独占的套装
        for (EquipmentSetBranch setData : selectedSets) {
            EquipmentSet equipmentSet = findEquipmentSetForData(setData);
            if (equipmentSet != null && !equipmentSet.getEquippableGroup().isExclusive(setData)) {
                applySetToArmorStand(setData);
            }
        }
    }

    private EquipmentSet findEquipmentSetForData(EquipmentSetBranch setData) {
        Map<ResourceLocation, EquipmentSet> allSets = EquipmentSetManager.getInstance().getAllResources();

        for (EquipmentSet set : allSets.values()) {
            if (set.getEquippableGroup().equippableMaps().inverse().containsKey(setData)) {
                return set;
            }
        }
        return null;
    }

    private void applySetToArmorStand(EquipmentSetBranch setData) {
        setData.requiredMatchCount()
                .ifPresentOrElse(count -> applyPartialSet(setData),() -> applyFullSet(setData));
    }

    private void applyPartialSet(EquipmentSetBranch setData) {
        List<Map<EquipmentSlot, ItemStack>> combinations = previewCombinations.get(setData);
        if (combinations != null && !combinations.isEmpty()) {
            int index = (int) ((System.currentTimeMillis() / ROTATION_INTERVAL) % combinations.size());
            Map<EquipmentSlot, ItemStack> currentPreview = combinations.get(index);
            for (Map.Entry<EquipmentSlot, ItemStack> entry : currentPreview.entrySet()) {
                if (previewArmorStand.getItemBySlot(entry.getKey()).isEmpty()) {
                    previewArmorStand.setItemSlot(entry.getKey(), entry.getValue().copy());
                }
            }
        }
    }

    private void applyFullSet(EquipmentSetBranch setData) {
        Map<IWearable, Ingredient> equipages = setData.equipages();
        for (Map.Entry<IWearable, Ingredient> entry : equipages.entrySet()) {
            if (entry.getKey() instanceof VanillaWearable vanillaEquippable) {
                EquipmentSlot slot = vanillaEquippable.slotType();
                if (previewArmorStand.getItemBySlot(slot).isEmpty()) {
                    ItemStack[] matchingStacks = entry.getValue().getItems();
                    if (matchingStacks.length > 0) {
                        previewArmorStand.setItemSlot(slot, matchingStacks[0].copy());
                    }
                }
            }
        }
    }

    private void updatePreviewCombinations(Set<EquipmentSetBranch> selectedSets) {
        previewCombinations.clear();
        for (EquipmentSetBranch setData : selectedSets) {
            setData.requiredMatchCount().ifPresent(count -> {
                List<Map<EquipmentSlot, ItemStack>> combinations = generatePreviewCombinations(setData);
                previewCombinations.put(setData, combinations);
            });
        }
    }

    private List<Map<EquipmentSlot, ItemStack>> generatePreviewCombinations(EquipmentSetBranch setData) {
        List<Map<EquipmentSlot, ItemStack>> combinations = new ArrayList<>();
        Map<IWearable, Ingredient> equipages = setData.equipages();
        Optional<Integer> requiredMatchCount = setData.requiredMatchCount();

        if (requiredMatchCount.isEmpty()) return combinations;
        
        Map<EquipmentSlot, List<ItemStack>> slotItems = new HashMap<>();
        for (Map.Entry<IWearable, Ingredient> entry : equipages.entrySet()) {
            if (entry.getKey() instanceof VanillaWearable(EquipmentSlot slotType)) {
                ItemStack[] items = entry.getValue().getItems();
                if (items.length > 0) {
                    slotItems.put(slotType, Arrays.asList(items));
                }
            }
        }

        List<EquipmentSlot> availableSlots = new ArrayList<>(slotItems.keySet());
        if (availableSlots.size() >= requiredMatchCount.get()) {
            generateCombinationsHelper(new ArrayList<>(), availableSlots, 0, requiredMatchCount.get(), slotItems, combinations);
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
                combination.put(slot, items.get(0));
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

    public void renderPlayerEquipment(GuiGraphics guiGraphics, Player player) {
        if (previewArmorStand == null) return;

        clearArmorStandEquipment();
        
        // 复制玩家当前装备到盔甲架
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack playerItem = player.getItemBySlot(slot);
            if (!playerItem.isEmpty()) {
                previewArmorStand.setItemSlot(slot, playerItem.copy());
            }
        }
        
        renderArmorStand(guiGraphics);
    }
} 