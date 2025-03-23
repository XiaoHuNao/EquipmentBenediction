package com.xiaohunao.equipment_benediction.common.hook;

import com.google.common.collect.Maps;
import com.xiaohunao.equipment_benediction.common.interfaces.IBenediction;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DelayHookManager {
    private static final DelayHookManager INSTANCE = new DelayHookManager();

    private final Map<DelayHookWrapper, Long> delayHooks = Maps.newHashMap();

    public static DelayHookManager getInstance() {
        return INSTANCE;
    }

    private DelayHookManager() {
    }

    public void tick() {
        try {
            // 创建要执行的钩子列表和更新后的延迟映射
            List<DelayHookWrapper> hooksToExecute = new ArrayList<>();
            Map<DelayHookWrapper, Long> updatedDelayHooks = Maps.newHashMap();
            
            // 第一步：找出所有需要执行的钩子并更新剩余时间
            for (Map.Entry<DelayHookWrapper, Long> entry : delayHooks.entrySet()) {
                long remainingDelay = entry.getValue() - 1;
                
                if (remainingDelay <= 0) {
                    hooksToExecute.add(entry.getKey());
                } else {
                    System.out.println("Remaining delay for " + entry.getKey().owner + ": " + remainingDelay);
                    updatedDelayHooks.put(entry.getKey(), remainingDelay);
                }
            }
            
            // 替换为更新后的映射
            delayHooks.clear();
            delayHooks.putAll(updatedDelayHooks);
            
            // 第二步：执行所有需要执行的钩子
            for (DelayHookWrapper wrapper : hooksToExecute) {
                executeDelayHook(wrapper);
            }
        } catch (Exception e) {
            // 记录任何异常
            System.err.println("Error in tick method: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends IHook, R> void executeDelayHook(DelayHookWrapper wrapper) {
        try {
            DelayHook<?> delayHook = wrapper.delayHook;
            IBenediction owner = wrapper.owner;
            HookMapManager.HookExecutor<T, R> executor = (HookMapManager.HookExecutor<T, R>) wrapper.executor;
            
            T hook = (T) delayHook.getHook();
            executor.execute(owner, hook, null);
        } catch (Exception e) {
            // 记录异常但不中断其他钩子的执行
            System.err.println("Error executing delayed hook: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean isDelayed(IHook hook) {
        for (DelayHookWrapper wrapper : delayHooks.keySet()) {
            if (wrapper.delayHook.getHook().equals(hook)) {
                return true;
            }
        }
        return false;
    }

    public <T extends IHook, R> void register(IBenediction owner, DelayHook<?> delayHook, HookMapManager.HookExecutor<T,R> executor) {
        DelayHookWrapper wrapper = new DelayHookWrapper(delayHook, owner, executor);
        delayHooks.put(wrapper, delayHook.getDelay());
    }

    public static class DelayHookWrapper {
        private final DelayHook<?> delayHook;
        private final IBenediction owner;
        private final HookMapManager.HookExecutor<?, ?> executor;

        public DelayHookWrapper(DelayHook<?> delayHook, IBenediction owner, HookMapManager.HookExecutor<?, ?> executor) {
            this.delayHook = delayHook;
            this.owner = owner;
            this.executor = executor;
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DelayHookWrapper that = (DelayHookWrapper) o;
            return delayHook.equals(that.delayHook) && 
                   owner.equals(that.owner);
        }
        
        @Override
        public int hashCode() {
            return 31 * delayHook.hashCode() + owner.hashCode();
        }
    }
}
