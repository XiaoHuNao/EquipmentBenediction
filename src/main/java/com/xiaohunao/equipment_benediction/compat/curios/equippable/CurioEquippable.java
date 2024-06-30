package com.xiaohunao.equipment_benediction.compat.curios.equippable;


import com.xiaohunao.equipment_benediction.common.equipment_set.equippable.Equippable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.util.LazyOptional;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.ISlotType;
import top.theillusivec4.curios.api.type.capability.ICuriosItemHandler;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;
import top.theillusivec4.curios.api.type.util.ISlotHelper;

public class CurioEquippable extends Equippable<ISlotType> {
    public CurioEquippable(ISlotType iSlotType) {
        super(iSlotType);
    }

    @Override
    public boolean checkEquippable(LivingEntity livingEntity, Ingredient ingredient) {
        LazyOptional<ICuriosItemHandler> curiosInventory = CuriosApi.getCuriosHelper().getCuriosHandler(livingEntity);
        if (curiosInventory.isPresent()){
            return curiosInventory.orElseGet(() -> null).getCurios()
                    .entrySet()
                    .stream()
                    .filter(entry -> entry.getKey().equals(slotType.getIdentifier()))
                    .anyMatch(entry -> {
                        IDynamicStackHandler handlerStacks = entry.getValue().getStacks();
                        for (int i = 0; i < handlerStacks.getSlots(); i++) {
                            ItemStack stackInSlot = handlerStacks.getStackInSlot(i);
                            if (ingredient.test(stackInSlot)) {
                                return true;
                            }
                        }
                        return false;
                    });
        }
        return false;
    }

    public static CurioEquippable of(String slotType) {
        ISlotHelper slotHelper = CuriosApi.getSlotHelper();
        slotHelper.getSlotType(slotType).ifPresentOrElse(
                CurioEquippable::new,
                () -> {
                    throw new IllegalArgumentException("Invalid slot '" + slotType);
                }
        );
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof CurioEquippable curioEquippable) {
            return this.slotType.equals((curioEquippable.slotType));
        }
        return false;
    }

    @Override
    public int hashCode() {
        return slotType.hashCode();
    }
}
