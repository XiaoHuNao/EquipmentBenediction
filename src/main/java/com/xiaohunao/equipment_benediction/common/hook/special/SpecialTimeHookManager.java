package com.xiaohunao.equipment_benediction.common.hook.special;

import com.google.common.collect.Maps;
import com.xiaohunao.equipment_benediction.common.attachment.EntityHookManager;
import com.xiaohunao.equipment_benediction.common.hook.HookMapManager;
import com.xiaohunao.equipment_benediction.common.hook.IHook;
import com.xiaohunao.equipment_benediction.common.init.EBAttachments;
import com.xiaohunao.equipment_benediction.common.interfaces.IBenediction;
import com.xiaohunao.equipment_benediction.common.network.EntityHookManagerSyncPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Map;

public class SpecialTimeHookManager {
    private final EntityHookManager entityHookManager;
    private final Map<SpecialTimeHookWrapper, Long> specialTimeHooks =  Maps.newHashMap();

    public SpecialTimeHookManager(EntityHookManager entityHookManager) {
        this.entityHookManager = entityHookManager;
    }

    public Map<SpecialTimeHookWrapper, Long> getSpecialTimeHooks() {
        return specialTimeHooks;
    }

    public void addHook(SpecialTimeHookWrapper wrapper) {
        if (!specialTimeHooks.containsKey(wrapper)){
            specialTimeHooks.put(wrapper, wrapper.specialTimeHook().getTime());
        }
    }

    public void tick(Player player){
        for (Map.Entry<SpecialTimeHookWrapper, Long> entry : specialTimeHooks.entrySet()) {
            SpecialTimeHookWrapper wrapper = entry.getKey();
            Long remainingTime = entry.getValue();
            SpecialTimeHook specialTimeHook = wrapper.specialTimeHook();
            if (specialTimeHook.canExecuteHook(this, player, wrapper, remainingTime)) {
                specialTimeHook.executeSpecialHook(wrapper);
                player.setData(EBAttachments.ENTITY_HOOK_MANAGER, entityHookManager);
                if (!player.level().isClientSide) {
                    PacketDistributor.sendToPlayer((ServerPlayer) player, new EntityHookManagerSyncPayload(player.getId(), entityHookManager.serializeNBT(null)));
                }
            }
        }
    }

    public <T extends IHook, R> boolean containsSpecialTimeHook(IBenediction owner, T hook, HookMapManager.HookExecutor<T, R> executor) {
        for (SpecialTimeHookWrapper wrapper : specialTimeHooks.keySet()) {
            boolean ownerBoolean = wrapper.owner().equals(owner);
            boolean hookBoolean = wrapper.specialTimeHook().getHook().equals(hook);
            boolean executable = wrapper.executor().equals(executor);
            if (ownerBoolean || hookBoolean || executable) {
                return true;
            }
        }
        return false;
    }
}
