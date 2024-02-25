package com.pancake.equipment_benediction.client.inventory;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffers;

public class ModifierMerchantMenu extends MerchantMenu {
    public ModifierMerchantMenu(int p_40036_, Inventory p_40037_, Merchant p_40038_) {
        super(p_40036_,p_40037_,p_40038_);
    }

    // 设置是否显示交易进度条
    public void setShowProgressBar(boolean p_40049_) {
        this.showProgressBar = p_40049_;
    }
    // 当槽位改变时触发，更新交易容器中的出售物品
    public void slotsChanged(Container p_40040_) {
        this.tradeContainer.updateSellItem();
        super.slotsChanged(p_40040_);
    }
    // 设置交易提示
    public void setSelectionHint(int p_40064_) {
        this.tradeContainer.setSelectionHint(p_40064_);
    }
    // 检查玩家是否仍然在与该商人交易
    public boolean stillValid(Player p_40042_) {
        return this.trader.getTradingPlayer() == p_40042_;
    }
    // 获取商人的经验值
    public int getTraderXp() {
        return this.trader.getVillagerXp();
    }
    // 获取商人的未来经验值
    public int getFutureTraderXp() {
        return this.tradeContainer.getFutureXp();
    }
    // 设置商人的经验值
    public void setXp(int p_40067_) {
        this.trader.overrideXp(p_40067_);
    }
    // 获取商人的等级
    public int getTraderLevel() {
        return this.merchantLevel;
    }
    // 设置商人的等级
    public void setMerchantLevel(int p_40070_) {
        this.merchantLevel = p_40070_;
    }
    // 设置商人是否可以重新进货
    public void setCanRestock(boolean p_40059_) {
        this.canRestock = p_40059_;
    }
    // 检查商人是否可以重新进货
    public boolean canRestock() {
        return this.canRestock;
    }
    // 检查是否可以一次性取出物品
    public boolean canTakeItemForPickAll(ItemStack p_40044_, Slot p_40045_) {
        return false;
    }
    // 移动物品堆栈
    public ItemStack quickMoveStack(Player p_40053_, int p_40054_) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(p_40054_);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (p_40054_ == 2) {
                if (!this.moveItemStackTo(itemstack1, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(itemstack1, itemstack);
                this.playTradeSound();
            } else if (p_40054_ != 0 && p_40054_ != 1) {
                if (p_40054_ >= 3 && p_40054_ < 30) {
                    if (!this.moveItemStackTo(itemstack1, 30, 39, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (p_40054_ >= 30 && p_40054_ < 39 && !this.moveItemStackTo(itemstack1, 3, 30, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 3, 39, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(p_40053_, itemstack1);
        }

        return itemstack;
    }
    // 播放交易音效
    private void playTradeSound() {
        if (!this.trader.isClientSide()) {
            Entity entity = (Entity)this.trader;
            entity.level().playLocalSound(entity.getX(), entity.getY(), entity.getZ(), this.trader.getNotifyTradeSound(), SoundSource.NEUTRAL, 1.0F, 1.0F, false);
        }

    }
    // 界面关闭时触发，处理商人物品的移动和丢弃
    public void removed(Player p_40051_) {
        super.removed(p_40051_);
        this.trader.setTradingPlayer((Player)null);
        if (!this.trader.isClientSide()) {
            if (!p_40051_.isAlive() || p_40051_ instanceof ServerPlayer && ((ServerPlayer)p_40051_).hasDisconnected()) {
                ItemStack itemstack = this.tradeContainer.removeItemNoUpdate(0);
                if (!itemstack.isEmpty()) {
                    p_40051_.drop(itemstack, false);
                }

                itemstack = this.tradeContainer.removeItemNoUpdate(1);
                if (!itemstack.isEmpty()) {
                    p_40051_.drop(itemstack, false);
                }
            } else if (p_40051_ instanceof ServerPlayer) {
                p_40051_.getInventory().placeItemBackInInventory(this.tradeContainer.removeItemNoUpdate(0));
                p_40051_.getInventory().placeItemBackInInventory(this.tradeContainer.removeItemNoUpdate(1));
            }

        }
    }
    // 尝试移动物品
    public void tryMoveItems(int p_40073_) {
        if (p_40073_ >= 0 && this.getOffers().size() > p_40073_) {
            ItemStack itemstack = this.tradeContainer.getItem(0);
            if (!itemstack.isEmpty()) {
                if (!this.moveItemStackTo(itemstack, 3, 39, true)) {
                    return;
                }

                this.tradeContainer.setItem(0, itemstack);
            }

            ItemStack itemstack1 = this.tradeContainer.getItem(1);
            if (!itemstack1.isEmpty()) {
                if (!this.moveItemStackTo(itemstack1, 3, 39, true)) {
                    return;
                }

                this.tradeContainer.setItem(1, itemstack1);
            }

            if (this.tradeContainer.getItem(0).isEmpty() && this.tradeContainer.getItem(1).isEmpty()) {
                ItemStack itemstack2 = this.getOffers().get(p_40073_).getCostA();
                this.moveFromInventoryToPaymentSlot(0, itemstack2);
                ItemStack itemstack3 = this.getOffers().get(p_40073_).getCostB();
                this.moveFromInventoryToPaymentSlot(1, itemstack3);
            }

        }
    }
    // 从玩家背包移动物品到交易槽位
    private void moveFromInventoryToPaymentSlot(int p_40061_, ItemStack p_40062_) {
        if (!p_40062_.isEmpty()) {
            for(int i = 3; i < 39; ++i) {
                ItemStack itemstack = this.slots.get(i).getItem();
                if (!itemstack.isEmpty() && ItemStack.isSameItemSameTags(p_40062_, itemstack)) {
                    ItemStack itemstack1 = this.tradeContainer.getItem(p_40061_);
                    int j = itemstack1.isEmpty() ? 0 : itemstack1.getCount();
                    int k = Math.min(p_40062_.getMaxStackSize() - j, itemstack.getCount());
                    ItemStack itemstack2 = itemstack.copy();
                    int l = j + k;
                    itemstack.shrink(k);
                    itemstack2.setCount(l);
                    this.tradeContainer.setItem(p_40061_, itemstack2);
                    if (l >= p_40062_.getMaxStackSize()) {
                        break;
                    }
                }
            }
        }

    }
    // 设置商人的交易内容
    public void setOffers(MerchantOffers p_40047_) {
        this.trader.overrideOffers(p_40047_);
    }
    // 获取商人的交易内容
    public MerchantOffers getOffers() {
        return this.trader.getOffers();
    }
    // 检查是否显示交易进度条
    public boolean showProgressBar() {
        return this.showProgressBar;
    }


}
