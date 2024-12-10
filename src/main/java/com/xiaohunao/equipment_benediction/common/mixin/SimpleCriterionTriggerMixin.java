package com.xiaohunao.equipment_benediction.common.mixin;


import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.critereon.EnterBlockTrigger;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.loot.LootContext;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

@Mixin(SimpleCriterionTrigger.class)
public abstract class SimpleCriterionTriggerMixin <T extends SimpleCriterionTrigger.SimpleInstance> implements CriterionTrigger<T> {


    @Inject(method = "trigger",at = @At(value = "HEAD"))
    public void trigger(ServerPlayer player, Predicate<T> testTrigger, CallbackInfo ci){
        SimpleCriterionTrigger<T> simpleCriterionTrigger = (SimpleCriterionTrigger<T>)(Object)(this);
        if (simpleCriterionTrigger instanceof PlayerTrigger) return;
        if (simpleCriterionTrigger instanceof EnterBlockTrigger) return;

        System.out.println(simpleCriterionTrigger);

        CriteriaTriggers.CODEC.encodeStart(NbtOps.INSTANCE,simpleCriterionTrigger).result().ifPresent(nbt ->{
            System.out.println(nbt);
        });


    }
}
