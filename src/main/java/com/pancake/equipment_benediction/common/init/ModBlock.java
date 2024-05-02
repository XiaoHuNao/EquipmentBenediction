package com.pancake.equipment_benediction.common.init;

import com.pancake.equipment_benediction.EquipmentBenediction;
import com.pancake.equipment_benediction.common.block.ReforgedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlock {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, EquipmentBenediction.MOD_ID);

    public static final RegistryObject<Block> REFORGED_BLOCK = BLOCKS.register("reforged_block", ReforgedBlock::new);
}
