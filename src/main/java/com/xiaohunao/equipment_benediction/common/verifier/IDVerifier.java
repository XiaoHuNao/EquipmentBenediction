package com.xiaohunao.equipment_benediction.common.verifier;

import com.mojang.serialization.MapCodec;
import com.xiaohunao.equipment_benediction.common.init.EQMapCodecs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public record IDVerifier(ResourceLocation id) implements IVerifier {
    public final static MapCodec<IDVerifier> CODEC = ResourceLocation.CODEC.fieldOf("verifier").xmap(IDVerifier::new, IDVerifier::id);

    @Override
    public boolean verify(ItemStack stack) {
        return BuiltInRegistries.ITEM.getKey(stack.getItem()).equals(id);
    }

    @Override
    public MapCodec<? extends IVerifier> mapCodec() {
        return EQMapCodecs.ID_VERIFIER_CODEC.get();
    }
}
