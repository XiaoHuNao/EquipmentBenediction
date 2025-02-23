package com.xiaohunao.equipment_benediction.common.modifier;

import com.xiaohunao.equipment_benediction.common.hook.HookMap;
import com.xiaohunao.equipment_benediction.common.hook.HookMapManager;

public class Modifier {
    protected final HookMap hookMap;

    public Modifier() {
        HookMap.Builder hookBuilder = HookMap.builder();
        init(hookBuilder);
        this.hookMap = hookBuilder.build();
    }
    protected Modifier(HookMap hooks) {
        this.hookMap = hooks;
        HookMapManager.getInstance().register(this, hookMap);
    }

    protected void init(HookMap.Builder hookBuilder) {

    }

    public HookMap getHookMap() {
        return hookMap;
    }
}
