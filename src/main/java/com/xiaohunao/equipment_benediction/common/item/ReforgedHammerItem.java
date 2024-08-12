package com.xiaohunao.equipment_benediction.common.item;


import com.xiaohunao.equipment_benediction.common.block.entity.ReforgedBlockEntity;
import com.xiaohunao.equipment_benediction.common.init.ModQuality;
import com.xiaohunao.equipment_benediction.common.quality.Quality;
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
            if (equippedItem.isEmpty() || reforgedItem.isEmpty()) return InteractionResult.FAIL;

            for (Quality value : ModQuality.QUALITY_MAP.values()) {
                Ingredient recastingStack = value.getRecastingStack();
                if (recastingStack.test(reforgedItem)) {
                    for (int i = 0; i < recastingStack.getItems().length; i++) {
                        ItemStack item = recastingStack.getItems()[i];
                        if (reforgedItem.getCount() >= item.getCount()) {
                            reforgedBlockEntity.recastingQuality();
                            level.playSound(null, context.getClickedPos(), SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 0.7F, 1.0F);
                            stack.hurtAndBreak(1, context.getPlayer(), (player) -> player.broadcastBreakEvent(context.getHand()));
                            reforgedItem.shrink(item.getCount());

                            for (int j = 0; j < 20; j++) {
                                double x = clickedPos.getX() + level.random.nextDouble();
                                double y = clickedPos.getY() + 1.0D;
                                double z = clickedPos.getZ() + level.random.nextDouble();
                                double velocityX = (level.random.nextDouble() - 0.5D) * 0.2D;
                                double velocityY = level.random.nextDouble() * 0.2D;
                                double velocityZ = (level.random.nextDouble() - 0.5D) * 0.2D;
                                level.addParticle(ParticleTypes.FLAME, x, y, z, velocityX, velocityY, velocityZ);
                            }

                            return InteractionResult.PASS;
                        }
                    }
                }
            }
        }
        return super.onItemUseFirst(stack, context);
    }
}
