package com.xiaohunao.equipment_benediction.common.mixin;


import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.xiaohunao.equipment_benediction.client.gui.widget.TransparentButton;
import com.xiaohunao.equipment_benediction.client.gui.screen.switcher.EquipmentSetSwitcherScreen;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends EffectRenderingInventoryScreen<InventoryMenu>{
    @Unique
    public Button equipmentBenediction$setSwitching;


    public InventoryScreenMixin(InventoryMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Inject(method = "init", at = @At("RETURN"))
    private void init(CallbackInfo info) {
        this.equipmentBenediction$setSwitching = new TransparentButton(
            this.leftPos + 32, this.topPos + 12,
            36, 65,
            Component.translatable("equipment.benediction.switch_set"),
            (button) -> {
                if (this.minecraft != null && this.minecraft.player != null) {
                    this.minecraft.setScreen(new EquipmentSetSwitcherScreen(this.minecraft.player));
                }
            }
        );
        this.addWidget(this.equipmentBenediction$setSwitching);
    }


    @Inject(method = "renderBg", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/InventoryScreen;renderEntityInInventoryFollowsMouse(Lnet/minecraft/client/gui/GuiGraphics;IIIIIFFFLnet/minecraft/world/entity/LivingEntity;)V", shift = At.Shift.AFTER))
    private void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY, CallbackInfo ci) {
        this.equipmentBenediction$setSwitching.render(guiGraphics, mouseX, mouseY, partialTick);
    }


}