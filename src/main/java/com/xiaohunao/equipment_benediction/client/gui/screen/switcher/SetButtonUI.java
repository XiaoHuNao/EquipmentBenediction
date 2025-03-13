package com.xiaohunao.equipment_benediction.client.gui.screen.switcher;

import com.google.common.collect.Multimap;
import com.google.common.collect.HashMultimap;
import com.xiaohunao.equipment_benediction.EquipmentBenediction;
import com.xiaohunao.equipment_benediction.api.manager.EquipmentSetManager;
import com.xiaohunao.equipment_benediction.common.attachment.EntityHookManager;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSet;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquippableGroup;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquippableSetData;
import com.xiaohunao.equipment_benediction.client.gui.widget.EquippableSetButton;
import com.xiaohunao.equipment_benediction.common.init.EBAttachments;
import com.xiaohunao.equipment_benediction.common.network.EntityHookManagerSyncPayload;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.client.Minecraft;
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

    public void initButtons(Set<EquipmentSet> equipmentSets) {
        setButtons.clear();
        selectedSets.clear();
        
        int buttonX = leftPos + EquipmentSetSwitcherScreen.CONTENT_AREA_X;
        int buttonY = topPos + EquipmentSetSwitcherScreen.CONTENT_AREA_Y;
        int totalHeight = 0;

        for (EquipmentSet equipmentSet : equipmentSets) {
            EntityHookManager hookManager = player.getData(EBAttachments.ENTITY_HOOK_MANAGER);
            Collection<EquippableSetData> equippableSetData = hookManager.getSetHookManager().getActivatedEquipped().get(equipmentSet);


            buttonY += GROUP_SPACING;
            buttonY += TITLE_HEIGHT;

            EquippableGroup equippableGroup = equipmentSet.getEquippableGroup();
            Set<EquippableSetData> equippableSets = equippableGroup.getEquippableSets();
            
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
                buttonY += SET_BUTTON_HEIGHT + BUTTON_SPACING;
                totalHeight += SET_BUTTON_HEIGHT + BUTTON_SPACING;
            }
            
            totalHeight += GROUP_SPACING + TITLE_HEIGHT;
        }

        maxScroll = Math.max(0, totalHeight - EquipmentSetSwitcherScreen.CONTENT_AREA_HEIGHT);
    }

    public void renderEquipmentSets(GuiGraphics guiGraphics, int mouseX, int mouseY, float scrollOffset, float partialTick) {
        int buttonY = topPos + EquipmentSetSwitcherScreen.CONTENT_AREA_Y - (int)(maxScroll * scrollOffset);
        int buttonX = leftPos + EquipmentSetSwitcherScreen.CONTENT_AREA_X;

        EquipmentSet currentSet = null;
        EquipmentSet lastSet = null;

        for (EquippableSetButton button : setButtons) {
            if (button.getEquipmentSet() != currentSet) {
                if (lastSet != null) {
                    renderSeparator(guiGraphics, buttonX, buttonY);
                    buttonY += GROUP_SPACING;
                }
                
                lastSet = currentSet;
                currentSet = button.getEquipmentSet();
                buttonY = renderSetTitle(guiGraphics, currentSet, buttonX, buttonY);
            }

            if (isButtonVisible(buttonY)) {
                button.setX(buttonX);
                button.setY(buttonY);
                button.render(guiGraphics, mouseX, mouseY, partialTick);
            }
            
            buttonY += SET_BUTTON_HEIGHT + BUTTON_SPACING;
        }

        if (currentSet != null) {
            renderSeparator(guiGraphics, buttonX, buttonY);
        }
    }

    private int renderSetTitle(GuiGraphics guiGraphics, EquipmentSet equipmentSet, int x, int y) {
        int titleBottom = y + TITLE_HEIGHT;
        int contentAreaTop = topPos + EquipmentSetSwitcherScreen.CONTENT_AREA_Y;
        int contentAreaBottom = contentAreaTop + EquipmentSetSwitcherScreen.CONTENT_AREA_HEIGHT;
        
        if (!(y > contentAreaBottom || titleBottom < contentAreaTop)) {
            ResourceLocation resource = EquipmentSetManager.getInstance().getResource(equipmentSet);
            String translationKey = "equipment_benediction.set_switcher." + resource.getNamespace() + "." + resource.getPath();
            Component name = Component.translatable(translationKey);
            
            int textWidth = Minecraft.getInstance().font.width(name);
            int textX = x + (SET_BUTTON_WIDTH - textWidth) / 2;
            
            guiGraphics.drawString(Minecraft.getInstance().font, name, textX, y, 0x808080);
        }
        
        return y + TITLE_HEIGHT;
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

    private void handleButtonClick(EquippableSetButton clickedButton,boolean selected) {
        EquipmentSet equipmentSet = clickedButton.getEquipmentSet();
        EntityHookManager entityHookManager = player.getData(EBAttachments.ENTITY_HOOK_MANAGER);
        entityHookManager.getSetHookManager().updateEquippable(equipmentSet, clickedButton.getSetData(), selected);
        player.setData(EBAttachments.ENTITY_HOOK_MANAGER, entityHookManager);
        PacketDistributor.sendToServer(new EntityHookManagerSyncPayload(player.getId(),entityHookManager.serializeNBT(null)));
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