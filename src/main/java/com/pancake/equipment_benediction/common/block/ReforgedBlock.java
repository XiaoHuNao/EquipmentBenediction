package com.pancake.equipment_benediction.common.block;

import com.pancake.equipment_benediction.common.block.entity.ReforgedBlockEntity;
import com.pancake.equipment_benediction.common.init.ModBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class ReforgedBlock extends BaseEntityBlock {
    public ReforgedBlock() {
        super(BlockBehaviour.Properties.of());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return ModBlockEntity.REFORGED_BLOCK_ENTITY.get().create(p_153215_, p_153216_);
    }
}
