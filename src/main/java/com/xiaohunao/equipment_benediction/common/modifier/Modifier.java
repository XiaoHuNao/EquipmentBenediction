package com.xiaohunao.equipment_benediction.common.modifier;

import com.xiaohunao.equipment_benediction.common.hook.HookMap;
import com.xiaohunao.equipment_benediction.common.hook.HookMapManager;
import com.xiaohunao.equipment_benediction.common.interfaces.IBenediction;
import net.minecraft.nbt.CompoundTag;

public class Modifier implements IBenediction {
    protected final HookMap hookMap;

    public Modifier() {
        HookMap.Builder hookBuilder = HookMap.builder();
        init(hookBuilder);
        this.hookMap = hookBuilder.build();
        HookMapManager.getInstance().register(this,hookMap);
    }
    protected Modifier(HookMap hooks) {
        this.hookMap = hooks;
        HookMapManager.getInstance().register(this,hookMap);
    }

    protected void init(HookMap.Builder hookBuilder) {

    }

    public HookMap getHookMap() {
        return hookMap;
    }
}
