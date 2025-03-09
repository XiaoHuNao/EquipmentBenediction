package com.xiaohunao.equipment_benediction.common.mixin;

import com.xiaohunao.equipment_benediction.client.gui.screen.switcher.EquipmentSetSwitcherScreen;
import com.xiaohunao.equipment_benediction.client.gui.widget.TransparentButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.CreativeModeTab;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreativeModeInventoryScreen.class)
public abstract class CreativeModeInventoryScreenMixin extends EffectRenderingInventoryScreen<CreativeModeInventoryScreen.ItemPickerMenu> {
    @Unique
    private Button equipmentBenediction$setSwitching;

    public CreativeModeInventoryScreenMixin(CreativeModeInventoryScreen.ItemPickerMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Inject(method = "init", at = @At("RETURN"))
    private void init(CallbackInfo info) {
        this.equipmentBenediction$setSwitching = new TransparentButton(
            this.leftPos + 73, this.topPos + 6 + 12,
            32, 43 - 24,
            Component.translatable("equipment.benediction.switch_set"),
            (button) -> {
                if (this.minecraft != null && this.minecraft.player != null) {
                    this.minecraft.setScreen(new EquipmentSetSwitcherScreen(this.minecraft.player));
                }
            }
        );
        this.addWidget(this.equipmentBenediction$setSwitching);
    }

    
    @Inject(method = "render", at = @At("RETURN"))
    private void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        CreativeModeInventoryScreen screen = (CreativeModeInventoryScreen) (Object) this;

        if (this.equipmentBenediction$setSwitching != null && CreativeModeInventoryScreen.selectedTab.getType() == CreativeModeTab.Type.INVENTORY) {
            this.equipmentBenediction$setSwitching.render(guiGraphics, mouseX, mouseY, partialTick);
        }
    }
}
