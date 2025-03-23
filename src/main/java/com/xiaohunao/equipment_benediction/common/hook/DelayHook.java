package com.xiaohunao.equipment_benediction.common.hook;

import com.xiaohunao.equipment_benediction.common.hook.dynamic.ISerializableHook;

public class DelayHook<T extends IHook> implements IHook {
    private final HookType<?> hookType;
    private final T hook;
    private final long delay;

    public DelayHook(HookType<?> hookType, T hook, long delay) {
        this.hookType = hookType;
        this.hook = hook;
        this.delay = delay;
    }

    public HookType<?> getHookType() {
        return hookType;
    }

    public T getHook() {
        return hook;
    }

    public long getDelay() {
        return delay;
    }
}
