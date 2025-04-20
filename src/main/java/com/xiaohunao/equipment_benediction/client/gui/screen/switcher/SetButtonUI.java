package com.xiaohunao.equipment_benediction.client.gui.screen.switcher;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.xiaohunao.equipment_benediction.EquipmentBenediction;
import com.xiaohunao.equipment_benediction.api.manager.EquipmentSetManager;
import com.xiaohunao.equipment_benediction.client.gui.widget.EquippableSetButton;
import com.xiaohunao.equipment_benediction.client.gui.widget.SetTitleButton;
import com.xiaohunao.equipment_benediction.common.attachment.EntityHookManager;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSet;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquippableGroup;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquippableSetData;
import com.xiaohunao.equipment_benediction.common.init.EBAttachments;
import com.xiaohunao.equipment_benediction.common.network.EntityHookManagerSyncPayload;
import com.xiaohunao.equipment_benediction.common.network.PostEquipOrUnequipEquipmentHookPayload;
import net.minecraft.client.Minecraft;
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
    private final List<SetTitleButton> titleButtons = new ArrayList<>();
    private final Map<EquipmentSet, List<EquippableSetButton>> setButtonsMap = new HashMap<>();
    private final Multimap<EquipmentSet, EquippableSetData> selectedSets = HashMultimap.create();
    private final int leftPos;
    private final int topPos;
    private final Player player;
    private int maxScroll = 0;

    public SetButtonUI(Player player, int leftPos, int topPos) {
        this.player = player;
        this.leftPos = leftPos;
        this.topPos = topPos;
    }

    private void updateMaxScroll() {
        int totalHeight = 0;
        
        for (int i = 0; i < titleButtons.size(); i++) {
            totalHeight += GROUP_SPACING;
            totalHeight += TITLE_HEIGHT;
            
            if (titleButtons.get(i).isExpanded() && i < setButtons.size()) {
                EquipmentSet set = setButtons.get(i).getEquipmentSet();
                List<EquippableSetButton> buttons = setButtonsMap.get(set);
                totalHeight += (SET_BUTTON_HEIGHT + BUTTON_SPACING) * buttons.size();
            }
        }
        
        maxScroll = Math.max(0, totalHeight - EquipmentSetSwitcherScreen.CONTENT_AREA_HEIGHT);
    }

    public void initButtons(Set<EquipmentSet> equipmentSets) {
        setButtons.clear();
        selectedSets.clear();
        titleButtons.clear();
        setButtonsMap.clear();
        
        int buttonX = leftPos + EquipmentSetSwitcherScreen.CONTENT_AREA_X;
        int buttonY = topPos + EquipmentSetSwitcherScreen.CONTENT_AREA_Y;

        for (EquipmentSet equipmentSet : equipmentSets) {
            EntityHookManager hookManager = player.getData(EBAttachments.ENTITY_HOOK_MANAGER);
            Collection<EquippableSetData> equippableSetData = hookManager.getSetHookManager().getActivatedEquipped().get(equipmentSet);

            buttonY += GROUP_SPACING;
            
            ResourceLocation resource = EquipmentSetManager.getInstance().getResource(equipmentSet);
            String translationKey = "equipment_benediction.set_switcher." + resource.getNamespace() + "." + resource.getPath();
            Component name = Component.translatable(translationKey);
            
            SetTitleButton titleButton = new SetTitleButton(
                buttonX, buttonY,
                SET_BUTTON_WIDTH, TITLE_HEIGHT,
                name,
                button -> {
                    SetTitleButton setTitleButton = (SetTitleButton) button;
                    setTitleButton.toggleExpanded();
                    updateMaxScroll();
                }
            );
            titleButtons.add(titleButton);
            
            buttonY += TITLE_HEIGHT;

            List<EquippableSetButton> equipButtons = new ArrayList<>();
            setButtonsMap.put(equipmentSet, equipButtons);

            EquippableGroup equippableGroup = equipmentSet.getEquippableGroup();
            Collection<EquippableSetData> equippableSets = equippableGroup.equippableMaps().values();
            
            for (EquippableSetData setData : equippableSets) {
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
                equipButtons.add(button);
                buttonY += SET_BUTTON_HEIGHT + BUTTON_SPACING;
            }
        }

        updateMaxScroll();
    }

    public void renderEquipmentSets(GuiGraphics guiGraphics, int mouseX, int mouseY, float scrollOffset, float partialTick) {
        int buttonY = topPos + EquipmentSetSwitcherScreen.CONTENT_AREA_Y - (int)(maxScroll * scrollOffset);
        int buttonX = leftPos + EquipmentSetSwitcherScreen.CONTENT_AREA_X;

        int titleIndex = 0;
        int currentY = buttonY;

        // 先渲染所有标题和对应的按钮
        for (SetTitleButton titleButton : titleButtons) {
            if (isButtonVisible(currentY)) {
                titleButton.setX(buttonX);
                titleButton.setY(currentY);
                titleButton.render(guiGraphics, mouseX, mouseY, partialTick);
            }
            currentY += TITLE_HEIGHT;

            // 如果标题是展开状态，渲染对应的按钮
            if (titleButton.isExpanded() && titleIndex < setButtons.size()) {
                EquipmentSet set = setButtons.get(titleIndex).getEquipmentSet();
                List<EquippableSetButton> buttons = setButtonsMap.get(set);
                
                for (EquippableSetButton button : buttons) {
                    if (isButtonVisible(currentY)) {
                        button.setX(buttonX);
                        button.setY(currentY);
                        button.render(guiGraphics, mouseX, mouseY, partialTick);
                    }
                    currentY += SET_BUTTON_HEIGHT + BUTTON_SPACING;
                }
            }

            currentY += GROUP_SPACING;
            titleIndex++;
        }

        // 渲染分隔线
        currentY = buttonY;
        for (int i = 0; i < titleButtons.size(); i++) {
            if (i > 0) {
                renderSeparator(guiGraphics, buttonX, currentY - GROUP_SPACING);
            }
            currentY += TITLE_HEIGHT;
            
            if (titleButtons.get(i).isExpanded() && i < setButtons.size()) {
                EquipmentSet set = setButtons.get(i).getEquipmentSet();
                List<EquippableSetButton> buttons = setButtonsMap.get(set);
                currentY += (SET_BUTTON_HEIGHT + BUTTON_SPACING) * buttons.size();
            }
            
            currentY += GROUP_SPACING;
        }
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // 检查标题按钮点击
        for (SetTitleButton titleButton : titleButtons) {
            if (titleButton.isMouseOver(mouseX, mouseY)) {
                return titleButton.mouseClicked(mouseX, mouseY, button);
            }
        }

        // 只检查展开的套装的按钮点击
        int titleIndex = 0;
        for (SetTitleButton titleButton : titleButtons) {
            if (titleButton.isExpanded() && titleIndex < setButtons.size()) {
                EquipmentSet set = setButtons.get(titleIndex).getEquipmentSet();
                List<EquippableSetButton> buttons = setButtonsMap.get(set);
                
                for (EquippableSetButton equipButton : buttons) {
                    if (equipButton.isMouseOver(mouseX, mouseY)) {
                        return equipButton.mouseClicked(mouseX, mouseY, button);
                    }
                }
            }
            titleIndex++;
        }

        return false;
    }

    private boolean isButtonVisible(int buttonY) {
        int contentAreaTop = topPos + EquipmentSetSwitcherScreen.CONTENT_AREA_Y;
        int contentAreaBottom = contentAreaTop + EquipmentSetSwitcherScreen.CONTENT_AREA_HEIGHT;
        int buttonBottom = buttonY + SET_BUTTON_HEIGHT;
        
        return !(buttonY > contentAreaBottom || buttonBottom < contentAreaTop);
    }

    private void handleSetButtonClick(EquippableSetButton clickedButton) {
        if (clickedButton.isExclusive()) {
            handleExclusiveButtonClick(clickedButton);
        } else {
            handleNonExclusiveButtonClick(clickedButton);
        }
    }

    private void handleButtonClick(EquippableSetButton clickedButton, boolean selected) {
        EquipmentSet equipmentSet = clickedButton.getEquipmentSet();
        EntityHookManager entityHookManager = player.getData(EBAttachments.ENTITY_HOOK_MANAGER);

        if (!selected){
            PacketDistributor.sendToServer(new PostEquipOrUnequipEquipmentHookPayload(selected));
        }

        entityHookManager.getSetHookManager()
                .updateEquippable(equipmentSet, clickedButton.getSetData(), selected)
                .updateSelectedEquipped(equipmentSet, clickedButton.getSetData(), selected)
                .sync(player);

        PacketDistributor.sendToServer(new EntityHookManagerSyncPayload(player.getId(),entityHookManager.serializeNBT(null)));

        if (selected){
            PacketDistributor.sendToServer(new PostEquipOrUnequipEquipmentHookPayload(selected));
        }
    }

    private void handleExclusiveButtonClick(EquippableSetButton clickedButton) {
        EquipmentSet equipmentSet = clickedButton.getEquipmentSet();
        EquippableSetData setData = clickedButton.getSetData();
        
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
        EquippableSetData setData = clickedButton.getSetData();
        
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

    public int getMaxScroll() {
        return maxScroll;
    }

    public Set<EquippableSetData> getSelectedSets() {
        return setButtons.stream()
            .filter(EquippableSetButton::isSelected)
            .map(EquippableSetButton::getSetData)
            .collect(java.util.stream.Collectors.toSet());
    }
    
    public Multimap<EquipmentSet, EquippableSetData> getSelectedSetsMap() {
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