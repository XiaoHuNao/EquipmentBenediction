package com.xiaohunao.equipment_benediction.common.block.entity;

import com.xiaohunao.equipment_benediction.api.IQuality;
import com.xiaohunao.equipment_benediction.common.init.ModBlockEntity;
import com.xiaohunao.equipment_benediction.common.init.ModQuality;
import com.xiaohunao.equipment_benediction.common.quality.QualityHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.util.Map;

public class ReforgedBlockEntity extends BlockEntity {
    private float renderOffset = 0.0F;
    private boolean isRenderQualityTip = false;
    private IQuality renderQuality = null;


    private final ItemStackHandler inventory = new ItemStackHandler(2){
        @Override
        protected void onContentsChanged(int slot) {
            inventoryChanged();
        }
    };

    public ReforgedBlockEntity(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
    }
    public ReforgedBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(ModBlockEntity.REFORGED_BLOCK_ENTITY.get(), p_155229_, p_155230_);
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        inventory.deserializeNBT(compound.getCompound("Inventory"));
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        compound.put("Inventory", inventory.serializeNBT());
    }

    public IItemHandler getInventory() {
        return inventory;
    }

    public float getRenderOffset() {
        return renderOffset;
    }

    public void setRenderOffset(float renderOffset) {
        this.renderOffset = renderOffset;
    }

    @Override
    @Nullable
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        load(pkt.getTag());
    }

    protected void inventoryChanged() {
        super.setChanged();
        if (level != null)
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
    }

    public ItemStack removeItem(int index) {
        return inventory.extractItem(index, getReforgedItem().getMaxStackSize(), false);
    }

    public ItemStack addEquippedItem(ItemStack addedStack, @Nullable Player player) {
        for (Map.Entry<ResourceKey<IQuality>, IQuality> entry : ModQuality.REGISTRY.get().getEntries()) {
            IQuality quality = entry.getValue();
            if (quality.isViable().test(addedStack)) {
                return inventory.insertItem(0, addedStack.copy(), false);
            }
        }
        return addedStack;
    }
    public ItemStack addReforgedItem(ItemStack equippedItem,ItemStack addedStack, @Nullable Player player) {
        for (Map.Entry<ResourceKey<IQuality>, IQuality> entry : ModQuality.REGISTRY.get().getEntries()) {
            IQuality quality = entry.getValue();
            if (quality.isViable().test(equippedItem) && quality.getRecastingStack().test(addedStack)) {
                return inventory.insertItem(1, addedStack.copy(), false);
            }
        }
        return addedStack;
    }

    public boolean isRenderQualityTip() {
        return isRenderQualityTip;
    }

    public void setRenderQualityTip(boolean renderQualityTip) {
        isRenderQualityTip = renderQualityTip;
    }

    public IQuality getRenderQuality() {
        return renderQuality;
    }

    public void setRenderQuality(IQuality renderQuality) {
        this.renderQuality = renderQuality;
    }

    public ItemStack getEquippedItem() {
        return inventory.getStackInSlot(0);
    }
    public ItemStack getReforgedItem() {
        return inventory.getStackInSlot(1);
    }

    public void recastingQuality() {
        if (level != null) {
            ItemStack itemStack = this.inventory.getStackInSlot(0);
            this.renderOffset = 0.0F;
            this.isRenderQualityTip = true;
            this.renderQuality = QualityHelper.generateRandomQuality(itemStack);
            QualityHelper.initModifier(itemStack,renderQuality);
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState blockState, ReforgedBlockEntity reforgedBlockEntity) {
    }
}
