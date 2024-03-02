package com.pancake.equipment_benediction.common.init;

import com.pancake.equipment_benediction.EquipmentBenediction;
import com.pancake.equipment_benediction.api.IModifier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;


import java.util.function.Supplier;

public class ModModifiers {
    public static final ResourceKey<Registry<IModifier>> MODIFIER_KEY = EquipmentBenediction.asResourceKey("modifier");
    public static final DeferredRegister<IModifier> MODIFIER = DeferredRegister.create(MODIFIER_KEY, EquipmentBenediction.MOD_ID);
    public static final Supplier<IForgeRegistry<IModifier>> MODIFIER_REGISTRY = MODIFIER.makeRegistry(RegistryBuilder::new);


}
