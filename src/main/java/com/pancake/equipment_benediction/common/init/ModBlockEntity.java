package com.pancake.equipment_benediction.common.init;

import com.pancake.equipment_benediction.EquipmentBenediction;
import com.pancake.equipment_benediction.common.block.entity.ReforgedBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntity {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, EquipmentBenediction.MOD_ID);

     public static final RegistryObject<BlockEntityType<ReforgedBlockEntity>> REFORGED_BLOCK_ENTITY = BLOCK_ENTITY.register("reforged_block",
             () -> BlockEntityType.Builder.of(ReforgedBlockEntity::new, ModBlock.REFORGED_BLOCK.get()).build(null));

}
