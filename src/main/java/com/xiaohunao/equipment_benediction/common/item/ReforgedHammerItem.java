package com.xiaohunao.equipment_benediction.common.item;


import com.xiaohunao.equipment_benediction.common.block.entity.ReforgedBlockEntity;
import com.xiaohunao.equipment_benediction.common.init.ModQuality;
import com.xiaohunao.equipment_benediction.common.quality.Quality;
import com.xiaohunao.equipment_benediction.common.quality.QualityHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Map;

public class ReforgedHammerItem extends TieredItem {
    public ReforgedHammerItem() {
        super(Tiers.DIAMOND, new Properties().durability(150));
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        BlockEntity blockEntity = level.getBlockEntity(clickedPos);
        if (blockEntity instanceof ReforgedBlockEntity reforgedBlockEntity){
            ItemStack equippedItem = reforgedBlockEntity.getEquippedItem();
            ItemStack reforgedItem = reforgedBlockEntity.getReforgedItem();
            if (equippedItem.isEmpty()) return InteractionResult.FAIL;

            Quality quality = QualityHelper.getQuality(equippedItem);
            if (quality == null){
                processEmptyRecastingStack(reforgedBlockEntity, stack, context);
                return InteractionResult.PASS;
            }else {
                Ingredient recastingStack = quality.getRecastingStack();
                if (recastingStack.test(reforgedItem)) {
                    for (int i = 0; i < recastingStack.getItems().length; i++) {
                        ItemStack item = recastingStack.getItems()[i];
                        if (reforgedItem.getCount() >= item.getCount()) {
                            processEmptyRecastingStack(reforgedBlockEntity, stack, context);
                            reforgedItem.shrink(item.getCount());
                            return InteractionResult.PASS;
                        }
                    }
                }
                if (recastingStack.isEmpty()){
                    processEmptyRecastingStack(reforgedBlockEntity, stack, context);
                    return InteractionResult.PASS;
                }
            }

        }
        return super.onItemUseFirst(stack, context);
    }
    private void processEmptyRecastingStack(ReforgedBlockEntity reforgedBlockEntity, ItemStack stack, UseOnContext context) {
        reforgedBlockEntity.recastingQuality();
        playSoundAndBreakItem(stack, context);
        spawnParticles(context.getClickedPos(), context.getLevel());
    }

    private void playSoundAndBreakItem(ItemStack stack, UseOnContext context) {
        context.getLevel().playSound(null, context.getClickedPos(), SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 0.7F, 1.0F);
        stack.hurtAndBreak(1, context.getPlayer(), (player) -> player.broadcastBreakEvent(context.getHand()));
    }

    private void spawnParticles(BlockPos clickedPos, Level level) {
        for (int j = 0; j < 20; j++) {
            double x = clickedPos.getX() + level.random.nextDouble();
            double y = clickedPos.getY() + 1.0D;
            double z = clickedPos.getZ() + level.random.nextDouble();
            double velocityX = (level.random.nextDouble() - 0.5D) * 0.2D;
            double velocityY = level.random.nextDouble() * 0.2D;
            double velocityZ = (level.random.nextDouble() - 0.5D) * 0.2D;
            level.addParticle(ParticleTypes.FLAME, x, y, z, velocityX, velocityY, velocityZ);
        }
    }
}
