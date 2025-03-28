package com.xiaohunao.equipment_benediction.common.hook.special;

import com.xiaohunao.equipment_benediction.common.hook.HookMapManager;
import com.xiaohunao.equipment_benediction.common.interfaces.IBenediction;

public record SpecialTimeHookWrapper(SpecialTimeHook specialTimeHook, IBenediction owner, HookMapManager.HookExecutor<?, ?> executor) {
}