package com.xiaohunao.equipment_benediction.common.data.gen.provider;

import com.xiaohunao.equipment_benediction.common.hook.dynamic.KnockbackHook;
import com.xiaohunao.equipment_benediction.common.init.EBModifiers;
import com.xiaohunao.equipment_benediction.common.modifier.SerializableModifier;
import net.minecraft.data.PackOutput;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

public class EBModifierProvider extends AbstractModifierProvider {
    public EBModifierProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void addModifiers() {
        SerializableModifier knockbackModifier = createModifier(new KnockbackHook());
//        add(EBModifiers.KNOCKBACK, knockbackModifier);
    }
}
