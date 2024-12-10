package com.xiaohunao.equipment_benediction.common.verifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.xiaohunao.equipment_benediction.common.init.EQMapCodecs;
import net.minecraft.world.item.ItemStack;

public record ClassVerifier(String className) implements IVerifier {
    public final static MapCodec<ClassVerifier> CODEC = Codec.STRING.fieldOf("verifier").xmap(ClassVerifier::new, ClassVerifier::className);

    @Override
    public boolean verify(ItemStack stack) {
        try {
            Class<?> clazz = Class.forName(className);
            return clazz.isInstance(stack.getItem());
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @Override
    public MapCodec<? extends IVerifier> mapCodec() {
        return EQMapCodecs.CLASS_VERIFIER_CODEC.get();
    }
}
