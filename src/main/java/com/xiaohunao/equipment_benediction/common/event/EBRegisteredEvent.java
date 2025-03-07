package com.xiaohunao.equipment_benediction.common.event;

import com.xiaohunao.equipment_benediction.common.interfaces.IBenediction;
import com.xiaohunao.equipment_benediction.api.manager.EBAbstractManager;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

/**
 * 注册事件类，用于处理对象的注册
 * @param <T> 要注册的对象类型
 */
public class EBRegisteredEvent<T extends IBenediction<?>> extends Event implements IModBusEvent {
    private final EBRegistry<T> registry;

    public EBRegisteredEvent(EBRegistry<T> registry) {
        this.registry = registry;
    }

    public boolean isValidManager(EBAbstractManager<T> manager) {
        return registry.equals(manager);
    }

    /**
     * 注册静态对象
     * @param id 对象ID
     * @param value 对象实例
     */
    public void registerStatic(ResourceLocation id, T value) {
        registry.registerStatic(id, value);
    }

    /**
     * 注册预期的动态对象
     * @param id 对象ID
     */
    public void registerExpected(ResourceLocation id) {
        registry.registerExpected(id);
    }

    /**
     * 注册接口，定义基本的注册操作
     * @param <T> 要注册的对象类型
     */
    public interface EBRegistry<T> {
        /**
         * 注册对象
         * @param id 对象ID
         * @param value 对象实例
         */
        void registerStatic(ResourceLocation id, T value);

        /**
         * 注册预期的动态对象
         * @param id 对象ID
         */
        void registerExpected(ResourceLocation id);
    }

    /**
     * 创建注册事件
     * @param registry 注册表实现
     * @param <T> 要注册的对象类型
     * @return 注册事件实例
     */
    public static <T extends IBenediction<?>> EBRegisteredEvent<T> create(EBRegistry<T> registry) {
        return new EBRegisteredEvent<>(registry);
    }
}
