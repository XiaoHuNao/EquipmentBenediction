package com.xiaohunao.equipment_benediction.common.modifier;

import com.xiaohunao.equipment_benediction.common.hook.HookMap;
import com.xiaohunao.equipment_benediction.common.manager.EBAbstractManager;
import com.xiaohunao.equipment_benediction.common.manager.ModifierManager;

public class Modifier {
    protected final HookMap hookMap;

    public Modifier() {
        HookMap.Builder hookBuilder = HookMap.builder();
        registerHooks(hookBuilder);
        this.hookMap = hookBuilder.build();
    }
    protected Modifier(HookMap hooks) {
        this.hookMap = hooks;
    }

    protected void registerHooks(HookMap.Builder hookBuilder) {

    }

}
