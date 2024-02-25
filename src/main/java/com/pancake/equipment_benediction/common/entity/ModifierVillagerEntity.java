package com.pancake.equipment_benediction.common.entity;

import com.pancake.equipment_benediction.client.inventory.ModifierMerchantMenu;
import com.pancake.equipment_benediction.common.init.ModEntities;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;

import java.util.OptionalInt;

public class ModifierVillagerEntity extends Villager {
    public ModifierVillagerEntity(EntityType<? extends Villager> entityType, Level level) {
        super(entityType, level);
    }

    public ModifierVillagerEntity(Level level, VillagerType villagerType) {
        super(ModEntities.MODIFIER_VILLAGER.get(), level, villagerType);
    }

    @Override
    public void openTradingScreen(Player player, Component component, int level) {
        OptionalInt optionalint = player.openMenu(new SimpleMenuProvider((p_45298_, p_45299_, p_45300_) -> new ModifierMerchantMenu(p_45298_, p_45299_, this), component));
        if (optionalint.isPresent()) {
            MerchantOffers merchantoffers = this.getOffers();
            if (!merchantoffers.isEmpty()) {
                player.sendMerchantOffers(optionalint.getAsInt(), merchantoffers, level, this.getVillagerXp(), this.showProgressBar(), this.canRestock());
            }
        }

    }
}
