package com.xiaohunao.equipment_benediction.client.gui.screen.switcher;

import com.google.common.collect.Sets;
import com.xiaohunao.equipment_benediction.EquipmentBenediction;
import com.xiaohunao.equipment_benediction.api.manager.EquipmentSetManager;
import com.xiaohunao.equipment_benediction.client.gui.widget.LayoutExpandButton;
import com.xiaohunao.equipment_benediction.client.gui.widget.ShowAllSetButton;
import com.xiaohunao.equipment_benediction.common.equipment_set.EquipmentSet;
import com.xiaohunao.equipment_benediction.common.equippable.IWearable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.*;

public class EquipmentSetSwitcherScreen extends Screen {
    public static final ResourceLocation EXTRA_SLOTS = EquipmentBenediction.asResource("textures/gui/set_switcher/extra_slots.png");

    public static final int CONTENT_AREA_X = 8;
    public static final int CONTENT_AREA_Y = 8;
    public static final int CONTENT_AREA_WIDTH = 92;
    public static final int CONTENT_AREA_HEIGHT = 70;

    private static final int GRID_SPACING = 10;
    private static final int MAX_MODULES = 4;

    private final Player player;
    private final List<SwitcherModuleUI> modules;

    private LayoutExpandButton layoutExpandButton;
    private ShowAllSetButton showAllSetButton;

    public EquipmentSetSwitcherScreen(Player player) {
        super(Component.translatable("screen.equipment_benediction.equipment_set_switcher"));
        this.player = player;
        this.modules = new ArrayList<>();
    }

    @Override
    protected void init() {
        this.layoutExpandButton = this.addRenderableWidget(new LayoutExpandButton(
            0, 0,
            button -> toggleLayout())
        );
            
        this.showAllSetButton = this.addRenderableWidget(new ShowAllSetButton(
            0, 0,
            button -> {
                showAllSetButton.setShowAllSet(!showAllSetButton.isShowAllSet());
                updateModules();
            }));

        initializeModules();
        updateModulePositions();
    }

    private void initializeModules() {
        modules.clear();
        modules.add(new SwitcherModuleUI(player, 0, 0));

        if (layoutExpandButton.isGridLayout()) {
            for (int i = 1; i < MAX_MODULES; i++) {
                modules.add(new SwitcherModuleUI(player, 0, 0));
            }
        }

        updateModules();
    }

    private void updateModules() {
        Set<EquipmentSet> equipmentSets = getShowSet();
        List<EquipmentSet> setList = new ArrayList<>(equipmentSets);
        int moduleCount = modules.size();

        int setsPerModule = (int) Math.ceil((double) setList.size() / moduleCount);

        for (int i = 0; i < moduleCount; i++) {
            int startIndex = i * setsPerModule;
            int endIndex = Math.min(startIndex + setsPerModule, setList.size());
            
            if (startIndex < setList.size()) {
                List<EquipmentSet> moduleSets = setList.subList(startIndex, endIndex);
                modules.get(i).initButtons(new HashSet<>(moduleSets));
            } else {
                modules.get(i).initButtons(new HashSet<>());
            }
        }
    }

    private void updateModulePositions() {
        if (layoutExpandButton.isGridLayout()) {
            int moduleWidth = SwitcherModuleUI.WIDTH;
            int moduleHeight = SwitcherModuleUI.HEIGHT;
            int totalWidth = (moduleWidth * 2) + GRID_SPACING;
            int totalHeight = (moduleHeight * 2) + GRID_SPACING;
            
            int startX = (this.width - totalWidth) / 2;
            int startY = (this.height - totalHeight) / 2;

            if (showAllSetButton != null && layoutExpandButton != null) {
                showAllSetButton.setX(startX - ShowAllSetButton.BUTTON_SIZE);
                showAllSetButton.setY(startY);
                layoutExpandButton.setX(startX - LayoutExpandButton.BUTTON_SIZE);
                layoutExpandButton.setY(startY + ShowAllSetButton.BUTTON_SIZE);
            }

            Set<EquipmentSet> equipmentSets = getShowSet();
            List<EquipmentSet> setList = new ArrayList<>(equipmentSets);
            setList.sort(Comparator.comparing(set -> {
                ResourceLocation resource = EquipmentSetManager.getInstance().getResource(set);
                return resource.toString();
            }));
            
            int setsPerModule = setList.isEmpty() ? 0 : (int) Math.ceil((double) setList.size() / modules.size());

            for (int i = 0; i < modules.size(); i++) {
                int row = i / 2;
                int col = i % 2;
                int x = startX + (col * (moduleWidth + GRID_SPACING));
                int y = startY + (row * (moduleHeight + GRID_SPACING));

                int startIndex = i * setsPerModule;
                int endIndex = Math.min(startIndex + setsPerModule, setList.size());

                if (startIndex >= setList.size()) {
                    modules.get(i).initButtons(new LinkedHashSet<>());
                } else {
                    Set<EquipmentSet> moduleSets = new LinkedHashSet<>(setList.subList(startIndex, endIndex));
                    modules.get(i).initButtons(moduleSets);
                }
                
                modules.get(i).setPosition(x, y);
            }
        } else {
            int x = (this.width - SwitcherModuleUI.WIDTH) / 2;
            int y = (this.height - SwitcherModuleUI.HEIGHT) / 2;

            Set<EquipmentSet> equipmentSets = getShowSet();
            List<EquipmentSet> setList = new ArrayList<>(equipmentSets);

            setList.sort(Comparator.comparing(set -> {
                ResourceLocation resource = EquipmentSetManager.getInstance().getResource(set);
                return resource.toString();
            }));
            
            modules.get(0).setPosition(x, y);
            modules.get(0).initButtons(new LinkedHashSet<>(setList));


            if (showAllSetButton != null && layoutExpandButton != null) {
                showAllSetButton.setX(x - ShowAllSetButton.BUTTON_SIZE);
                showAllSetButton.setY(y);
                layoutExpandButton.setX(x - LayoutExpandButton.BUTTON_SIZE);
                layoutExpandButton.setY(y + ShowAllSetButton.BUTTON_SIZE);
            }
        }
    }

    private void toggleLayout() {
        layoutExpandButton.setGridLayout(!layoutExpandButton.isGridLayout());
        initializeModules();
        updateModulePositions();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);

        for (SwitcherModuleUI module : modules) {
            module.render(guiGraphics, mouseX, mouseY, partialTick);
        }


        layoutExpandButton.render(guiGraphics, mouseX, mouseY, partialTick);
        showAllSetButton.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    private Set<EquipmentSet> getShowSet() {
        Set<EquipmentSet> equipmentSets = Sets.newHashSet();
        EquipmentSetManager setManager = EquipmentSetManager.getInstance();

        if (showAllSetButton.isShowAllSet()) {
            equipmentSets.addAll(setManager.getAllResources().values());
            return equipmentSets;
        }

        for (EquipmentSet set : setManager.getAllResources().values()) {
            setLoop:
            for (var branch : set.allBranch()) {
                for (Map.Entry<IWearable, Ingredient> entry : branch.equipages().entrySet()) {
                    if (entry.getKey().checkWearable(player, entry.getValue())) {
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
        for (SwitcherModuleUI module : modules) {
            module.removed();
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        EquipmentBenediction.LOGGER.debug("EquipmentSetSwitcherScreen mouseClicked: x={}, y={}, button={}", mouseX, mouseY, button);
        
        if (super.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }

        for (SwitcherModuleUI module : modules) {
            if (isMouseInModule(mouseX, mouseY, module)) {
                return module.mouseClicked(mouseX, mouseY, button);
            }
        }

        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (super.mouseReleased(mouseX, mouseY, button)) {
            return true;
        }

        for (SwitcherModuleUI module : modules) {
            if (isMouseInModule(mouseX, mouseY, module)) {
                return module.mouseReleased(mouseX, mouseY, button);
            }
        }

        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (super.mouseDragged(mouseX, mouseY, button, dragX, dragY)) {
            return true;
        }

        for (SwitcherModuleUI module : modules) {
            if (isMouseInModule(mouseX, mouseY, module)) {
                return module.mouseDragged(mouseX, mouseY, button, dragX, dragY);
            }
        }

        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) {
            return true;
        }

        for (SwitcherModuleUI module : modules) {
            if (isMouseInModule(mouseX, mouseY, module)) {
                return module.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
            }
        }

        return false;
    }

    private boolean isMouseInModule(double mouseX, double mouseY, SwitcherModuleUI module) {
        return mouseX >= module.leftPos && mouseX < module.leftPos + SwitcherModuleUI.WIDTH &&
               mouseY >= module.topPos && mouseY < module.topPos + SwitcherModuleUI.HEIGHT;
    }
} 