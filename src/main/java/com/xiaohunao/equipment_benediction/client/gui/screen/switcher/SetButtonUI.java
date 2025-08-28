package com.xiaohunao.equipment_benediction.client.gui.screen.switcher;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.xiaohunao.equipment_benediction.EquipmentBenediction;
import com.xiaohunao.equipment_benediction.api.manager.EquipmentSetManager;
import com.xiaohunao.equipment_benediction.client.gui.widget.EquippableSetButton;
import com.xiaohunao.equipment_benediction.client.gui.widget.SetTitleButton;
import com.xiaohunao.equipment_benediction.common.attachment.EntityHookManager;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSet;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSetBranch;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquippableGroup;
import com.xiaohunao.equipment_benediction.common.init.EBAttachments;
import com.xiaohunao.equipment_benediction.common.network.EntityHookManagerSyncPayload;
import com.xiaohunao.equipment_benediction.common.network.PostEquipOrUnequipEquipmentHookPayload;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.*;

public class SetButtonUI {
    public static final ResourceLocation SET_SEPARATOR_BAR = EquipmentBenediction.asResource("textures/gui/set_switcher/set_separator_bar.png");

    public static final int SET_BUTTON_WIDTH = 92;
    public static final int SET_BUTTON_HEIGHT = 18;

    public static final int BUTTON_SPACING = 1;
    public static final int TITLE_HEIGHT = 10;
    public static final int GROUP_SPACING = 4;

    private final List<EquippableSetButton> setButtons = new ArrayList<>();
    private final Map<EquipmentSet, SetTitleButton> titleButtons = new HashMap<>();
    private final Multimap<EquipmentSet, EquipmentSetBranch> selectedSets = HashMultimap.create();
    private final int leftPos;
    private final int topPos;
    private final Player player;
    private int maxScroll = 0;
    
    public SetButtonUI(Player player, int leftPos, int topPos) {
        this.player = player;
        this.leftPos = leftPos;
        this.topPos = topPos;
    }

    private void recalculateHeight() {
        int totalHeight = 0;

        // 使用LinkedHashMap保持顺序一致性
        Map<EquipmentSet, List<EquippableSetButton>> buttonsBySet = new LinkedHashMap<>();
        for (EquippableSetButton button : setButtons) {
            buttonsBySet.computeIfAbsent(button.getEquipmentSet(), k -> new ArrayList<>()).add(button);
        }

        boolean isFirst = true;
        for (Map.Entry<EquipmentSet, List<EquippableSetButton>> entry : buttonsBySet.entrySet()) {
            if (!isFirst) {
                totalHeight += GROUP_SPACING;
            }
            isFirst = false;

            EquipmentSet equipmentSet = entry.getKey();
            List<EquippableSetButton> buttons = entry.getValue();

            totalHeight += TITLE_HEIGHT;

            SetTitleButton titleButton = titleButtons.get(equipmentSet);
            if (titleButton != null && titleButton.isExpanded()) {
                totalHeight += (SET_BUTTON_HEIGHT + BUTTON_SPACING) * buttons.size();
            }
        }

        if (!buttonsBySet.isEmpty()) {
            totalHeight += GROUP_SPACING;
        }

        maxScroll = Math.max(0, totalHeight - EquipmentSetSwitcherScreen.CONTENT_AREA_HEIGHT);
    }

    public void initButtons(Set<EquipmentSet> equipmentSets) {
        setButtons.clear();
        titleButtons.clear();
        selectedSets.clear();
        
        int buttonX = leftPos + EquipmentSetSwitcherScreen.CONTENT_AREA_X;
        int buttonY = topPos + EquipmentSetSwitcherScreen.CONTENT_AREA_Y;

        for (EquipmentSet equipmentSet : equipmentSets) {
            EntityHookManager hookManager = player.getData(EBAttachments.ENTITY_HOOK_MANAGER);
            Collection<EquipmentSetBranch> equippableSetData = hookManager.getSetHookManager().getActivatedSetBranch().get(equipmentSet);

            buttonY += GROUP_SPACING;

            // 创建标题按钮
            ResourceLocation resource = EquipmentSetManager.getInstance().getResource(equipmentSet);
            String translationKey = "equipment_benediction.set_switcher." + resource.getNamespace() + "." + resource.getPath();
            Component name = Component.translatable(translationKey);
            
            SetTitleButton titleButton = new SetTitleButton(
                buttonX, buttonY,
                SET_BUTTON_WIDTH, TITLE_HEIGHT,
                name,
                button -> handleTitleButtonClick(equipmentSet)
            );
            titleButtons.put(equipmentSet, titleButton);
            buttonY += TITLE_HEIGHT;

            EquippableGroup equippableGroup = equipmentSet.getEquippableGroup();
            Collection<EquipmentSetBranch> equippableSets = equippableGroup.equippableMaps().values();
            
            for (EquipmentSetBranch setData : equippableSets) {
                boolean isExclusive = equippableGroup.isExclusive(setData);
                EquippableSetButton button = new EquippableSetButton(
                    buttonX, buttonY, 
                    SET_BUTTON_WIDTH,
                    SET_BUTTON_HEIGHT,
                    setData, equipmentSet, isExclusive,
                    clickedButton -> handleSetButtonClick((EquippableSetButton) clickedButton)
                );

                if (equippableSetData.contains(setData)){
                    button.setSelected(true);
                }
                setButtons.add(button);
                buttonY += SET_BUTTON_HEIGHT + BUTTON_SPACING;
            }
        }

        recalculateHeight();
    }

    private void handleTitleButtonClick(EquipmentSet equipmentSet) {
        SetTitleButton titleButton = titleButtons.get(equipmentSet);
        if (titleButton != null) {
            titleButton.toggleExpanded();
            recalculateHeight();
        }
    }

    public void renderEquipmentSets(GuiGraphics guiGraphics, int mouseX, int mouseY, float scrollOffset, float partialTick) {
        scrollOffset = Math.max(0.0F, Math.min(1.0F, scrollOffset));
        
        int buttonY = topPos + EquipmentSetSwitcherScreen.CONTENT_AREA_Y - (int)(maxScroll * scrollOffset);
        int buttonX = leftPos + EquipmentSetSwitcherScreen.CONTENT_AREA_X;

        EquipmentSet lastSet = null;

        Map<EquipmentSet, List<EquippableSetButton>> buttonsBySet = new LinkedHashMap<>();
        for (EquippableSetButton button : setButtons) {
            buttonsBySet.computeIfAbsent(button.getEquipmentSet(), k -> new ArrayList<>()).add(button);
        }

        for (Map.Entry<EquipmentSet, List<EquippableSetButton>> entry : buttonsBySet.entrySet()) {
            EquipmentSet equipmentSet = entry.getKey();
            List<EquippableSetButton> buttons = entry.getValue();

            if (lastSet != null) {
                if (isButtonVisible(buttonY)) {
                    renderSeparator(guiGraphics, buttonX, buttonY);
                }
                buttonY += GROUP_SPACING;
            }

            SetTitleButton titleButton = titleButtons.get(equipmentSet);
            if (titleButton != null && isButtonVisible(buttonY)) {
                titleButton.setX(buttonX);
                titleButton.setY(buttonY);
                titleButton.render(guiGraphics, mouseX, mouseY, partialTick);
            }
            buttonY += TITLE_HEIGHT;

            if (titleButton != null && titleButton.isExpanded()) {
                for (EquippableSetButton button : buttons) {
                    if (isButtonVisible(buttonY)) {
                        button.setX(buttonX);
                        button.setY(buttonY);
                        button.render(guiGraphics, mouseX, mouseY, partialTick);
                    }
                    buttonY += SET_BUTTON_HEIGHT + BUTTON_SPACING;
                }
            }

            lastSet = equipmentSet;
        }

        if (lastSet != null && isButtonVisible(buttonY)) {
            renderSeparator(guiGraphics, buttonX, buttonY);
        }
    }

    private boolean isButtonVisible(int buttonY) {
        int contentAreaTop = topPos + EquipmentSetSwitcherScreen.CONTENT_AREA_Y;
        int contentAreaBottom = contentAreaTop + EquipmentSetSwitcherScreen.CONTENT_AREA_HEIGHT;
        
        int buttonBottom = buttonY + SET_BUTTON_HEIGHT;
        return buttonY < contentAreaBottom && buttonBottom > contentAreaTop;
    }

    private void handleSetButtonClick(EquippableSetButton clickedButton) {
        if (clickedButton.isExclusive()) {
            handleExclusiveButtonClick(clickedButton);
        } else {
            handleNonExclusiveButtonClick(clickedButton);
        }
    }

    private void handleButtonClick(EquippableSetButton clickedButton,boolean selected) {
        EquipmentSet equipmentSet = clickedButton.getEquipmentSet();
        EntityHookManager entityHookManager = player.getData(EBAttachments.ENTITY_HOOK_MANAGER);

        if (!selected){
            PacketDistributor.sendToServer(new PostEquipOrUnequipEquipmentHookPayload(selected));
        }

        entityHookManager.getSetHookManager()
                .updateEquippable(player,equipmentSet, clickedButton.getSetData(), selected)
                .updateSelectedEquipped(equipmentSet, clickedButton.getSetData(), selected)
                .sync(player);

        PacketDistributor.sendToServer(new EntityHookManagerSyncPayload(player.getId(),entityHookManager.serializeNBT(null)));

        if (selected){
            PacketDistributor.sendToServer(new PostEquipOrUnequipEquipmentHookPayload(selected));
        }
    }

    private void handleExclusiveButtonClick(EquippableSetButton clickedButton) {
        EquipmentSet equipmentSet = clickedButton.getEquipmentSet();
        EquipmentSetBranch setData = clickedButton.getSetData();
        
        if (clickedButton.isSelected()) {
            clickedButton.setSelected(false);
            handleButtonClick(clickedButton, clickedButton.isSelected());
            selectedSets.remove(equipmentSet, setData);
        } else {
            setButtons.stream()
                .filter(button -> button.getEquipmentSet() == equipmentSet)
                .forEach(button -> {
                    button.setSelected(false);
                    handleButtonClick(button, button.isSelected());
                    selectedSets.remove(equipmentSet, button.getSetData());
                });
                
            clickedButton.setSelected(true);
            handleButtonClick(clickedButton, clickedButton.isSelected());
            selectedSets.put(equipmentSet, setData);
        }
    }

    private void handleNonExclusiveButtonClick(EquippableSetButton clickedButton) {
        EquipmentSet equipmentSet = clickedButton.getEquipmentSet();
        EquipmentSetBranch setData = clickedButton.getSetData();
        
        boolean hasExclusiveSelected = setButtons.stream()
            .anyMatch(button -> button.getEquipmentSet() == equipmentSet && 
                              button.isExclusive() && 
                              button.isSelected());

        if (hasExclusiveSelected) {
            setButtons.stream()
                .filter(button -> button.getEquipmentSet() == equipmentSet && 
                                button.isExclusive())
                .forEach(button -> {
                    button.setSelected(false);
                    handleButtonClick(button, button.isSelected());
                    selectedSets.remove(equipmentSet, button.getSetData());
                });
        }
        
        clickedButton.setSelected(!clickedButton.isSelected());
        handleButtonClick(clickedButton, clickedButton.isSelected());
        
        if (clickedButton.isSelected()) {
            selectedSets.put(equipmentSet, setData);
        } else {
            selectedSets.remove(equipmentSet, setData);
        }
    }

    public List<EquippableSetButton> getSetButtons() {
        return setButtons;
    }

    public Collection<SetTitleButton> getTitleButtons() {
        return titleButtons.values();
    }

    public int getMaxScroll() {
        return maxScroll;
    }

    public Set<EquipmentSetBranch> getSelectedSets() {
        return setButtons.stream()
            .filter(EquippableSetButton::isSelected)
            .map(EquippableSetButton::getSetData)
            .collect(java.util.stream.Collectors.toSet());
    }
    
    public Multimap<EquipmentSet, EquipmentSetBranch> getSelectedSetsMap() {
        return HashMultimap.create(selectedSets);
    }

    private void renderSeparator(GuiGraphics guiGraphics, int x, int y) {
        int contentAreaTop = topPos + EquipmentSetSwitcherScreen.CONTENT_AREA_Y;
        int contentAreaBottom = contentAreaTop + EquipmentSetSwitcherScreen.CONTENT_AREA_HEIGHT;
        
        if (!(y > contentAreaBottom || y < contentAreaTop)) {
            guiGraphics.blit(
                SET_SEPARATOR_BAR,
                x, y,
                0, 0,
                SET_BUTTON_WIDTH, 2,
                SET_BUTTON_WIDTH, 2
            );
        }
    }
} 