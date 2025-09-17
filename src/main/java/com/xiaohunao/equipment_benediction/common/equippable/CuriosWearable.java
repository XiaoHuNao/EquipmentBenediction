package com.xiaohunao.equipment_benediction.common.equippable;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.ISlotType;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

public record CuriosWearable(ISlotType curiosSlotType) implements IWearable{
    @Override
    public boolean checkWearable(LivingEntity livingEntity, Ingredient ingredient) {
        return CuriosApi.getCuriosInventory(livingEntity)
                .flatMap(curiosInventory -> curiosInventory.getStacksHandler(curiosSlotType.getIdentifier()))
                .map(ICurioStacksHandler::getStacks)
                .map(dynamicStackHandler -> {
                    int slots = dynamicStackHandler.getSlots();
                    for (int i = 0; i < slots; i++) {
                        ItemStack stack = dynamicStackHandler.getStackInSlot(i);
                        if (ingredient.test(stack)) {
                            return true;
                        }
                    }
                    return false;
                })
                .orElse(false);
    }

    @Override
    public MapCodec<? extends IWearable> codec() {
        return null;
    }

    @Override
    public ResourceLocation getIcon() {
        return curiosSlotType.getIcon();
    }

    @Override
    public Component getDesc() {
        return Component.translatable("curios." + "equipment_benediction.wearable." + curiosSlotType.getIdentifier());
    }
}
