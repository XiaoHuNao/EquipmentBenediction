package com.xiaohunao.equipment_benediction.common.mixin;

import com.xiaohunao.equipment_benediction.common.mixed.ILivingEntity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements ILivingEntity {
    @Unique
    private boolean equipment_benediction$firstSynced = false;

    @Override
    public boolean equipment_benediction$isFirstSynced() {
        return equipment_benediction$firstSynced;
    }

    @Inject(method = "detectEquipmentUpdates", at = @At("TAIL"))
    private void update(CallbackInfo ci) {
        this.equipment_benediction$firstSynced = true;
    }
}
