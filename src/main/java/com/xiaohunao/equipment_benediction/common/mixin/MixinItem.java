package com.xiaohunao.equipment_benediction.common.mixin;

import com.xiaohunao.equipment_benediction.common.quality.QualityHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public abstract class MixinItem {
	@Inject(method = "getName(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/network/chat/Component;", at = @At("RETURN"), cancellable = true)
	private void onGetDisplayName(ItemStack stack, CallbackInfoReturnable<Component> cir) {
		if (QualityHelper.hasQuality(stack)) {
			Component displayName = QualityHelper.getQuality(stack).getDisplayName();
			Component component = Component.translatable(displayName.getString()).append(" ").append(cir.getReturnValue()).setStyle(displayName.getStyle());
			cir.setReturnValue(component);
		}
	}
}