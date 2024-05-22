package com.pancake.equipment_benediction.common.block;

import com.pancake.equipment_benediction.common.block.entity.ReforgedBlockEntity;
import com.pancake.equipment_benediction.common.init.ModBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class ReforgedBlock extends BaseEntityBlock {
    public VoxelShape makeShape(){
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0, 0.5625, 0, 1, 1, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.25, 0.3125, 0.25, 0.75, 0.5625, 0.75), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.125, 0, 0.125, 0.875, 0.3125, 0.875), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.84375, 0.3125, 0.5, 1.03125, 0.5625, 0.5), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(-0.03125, 0.3125, 0.5, 0.15625, 0.5625, 0.5), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.84375, 0.3125, 0.5, 1.03125, 0.5625, 0.5), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(-0.03125, 0.3125, 0.5, 0.15625, 0.5625, 0.5), BooleanOp.OR);
        return shape;
    }


    public ReforgedBlock() {
        super(Block.Properties.of()
                .strength(0.5F, 6.0F)
                .sound(SoundType.LANTERN)
                .noOcclusion()
        );
    }

    @Override
    public VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
        return makeShape();
    }

    @Override
    public RenderShape getRenderShape(BlockState p_49232_) {
        return RenderShape.MODEL;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        BlockEntity tileEntity = level.getBlockEntity(pos);
        if (tileEntity instanceof ReforgedBlockEntity reforgedBlockEntity) {
            if (!level.isClientSide) {
                ItemStack itemInHand = player.getItemInHand(hand);
                EquipmentSlot slot = hand.equals(InteractionHand.MAIN_HAND) ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;


                ItemStack reforgedItem = reforgedBlockEntity.getReforgedItem();
                ItemStack equippedItem = reforgedBlockEntity.getEquippedItem();

                if (itemInHand.isEmpty()) {
                    int indexToRemove = reforgedItem.isEmpty() ? 0 : 1;
                    removeItem(reforgedBlockEntity, indexToRemove, player, slot, level, pos);
                    return InteractionResult.SUCCESS;
                } else {
                    ItemStack remainderStack;
                    if (!equippedItem.isEmpty()) {
                        remainderStack = reforgedBlockEntity.addReforgedItem(itemInHand, player);
                    } else {
                        remainderStack = reforgedBlockEntity.addEquippedItem(itemInHand, player);
                    }
                    handleItemResult(level, pos, player, remainderStack, itemInHand, slot);
                    return InteractionResult.SUCCESS;
                }
            }
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    public static void handleItemResult(Level level, BlockPos pos, Player player, ItemStack remainderStack, ItemStack heldStack, EquipmentSlot slot) {
        if (remainderStack.getCount() != heldStack.getCount()) {
            if (!player.isCreative()) {
                player.setItemSlot(slot, remainderStack);
            }
            level.playSound(null, pos, SoundEvents.LANTERN_PLACE, SoundSource.BLOCKS, 0.7F, 1.0F);
        }
    }

    public static void removeItem(ReforgedBlockEntity reforgedBlockEntity, int index, Player player, EquipmentSlot slot, Level level, BlockPos pos) {
        ItemStack extractedStack = reforgedBlockEntity.removeItem(index);
        if (!player.isCreative()) {
            player.setItemSlot(slot, extractedStack);
        }
        level.playSound(null, pos, SoundEvents.LANTERN_PLACE, SoundSource.BLOCKS, 0.7F, 1.0F);
    }


    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return ModBlockEntity.REFORGED_BLOCK_ENTITY.get().create(p_153215_, p_153216_);
    }

//    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153212_, BlockState p_153213_, BlockEntityType<T> p_153214_) {
        return createTickerHelper(p_153214_, ModBlockEntity.REFORGED_BLOCK_ENTITY.get(), ReforgedBlockEntity::tick);
    }
}
