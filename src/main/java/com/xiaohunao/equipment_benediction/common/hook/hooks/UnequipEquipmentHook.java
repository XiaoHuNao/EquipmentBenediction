package com.xiaohunao.equipment_benediction.common.hook.hooks;

import com.xiaohunao.equipment_benediction.common.hook.IHook;
import net.minecraft.world.entity.player.Player;

public interface UnequipEquipmentHook extends IHook {
    void onUnequipEquipment(Player player);
}
