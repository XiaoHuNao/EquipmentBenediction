package com.xiaohunao.equipment_benediction.common.hook;

import com.google.common.collect.*;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.xiaohunao.equipment_benediction.common.hook.dynamic.ISerializableHook;
import com.xiaohunao.equipment_benediction.common.init.EBRegistries;
import net.minecraft.resources.ResourceLocation;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class HookMap {
    private final Multimap<HookType<?>, IHook> hooks;
    private final BiMap<Integer, IHook> hooksById;

    private HookMap(Multimap<HookType<?>, IHook> hooks, BiMap<Integer, IHook> hooksById) {
        this.hooks = ImmutableMultimap.copyOf(hooks);
        this.hooksById = ImmutableBiMap.copyOf(hooksById);
    }

    @SuppressWarnings("unchecked")
    public <T extends IHook> Collection<T> get(HookType<T> type) {
        Set<T> result = new HashSet<>();
        hooks.forEach((hookType, hook) -> {
            if (type.getHookClass().isAssignableFrom(hook.getClass())) {
                result.add((T) hook);
            }
        });
        return result;
    }

    public <T extends IHook> Collection<T> get(Supplier<HookType<T>> type) {
        return get(type.get());
    }

    public boolean isEmpty() {
        return hooks.isEmpty();
    }

    public IHook getHookById(int id) {
        return hooksById.get(id);
    }

    public Integer getIdForHook(IHook hook) {
        return hooksById.inverse().get(hook);
    }


    public Set<Integer> getAllHookIds() {
        return hooksById.keySet();
    }

    public static Builder builder() {
        return new Builder();
    }

    public Multimap<HookType<?>, IHook> hooks() {
        return hooks;
    }

    public List<ISerializableHook> getSerializableHooks() {
        List<ISerializableHook> serializableHooks = new ArrayList<>();
        for (HookType<?> type : hooks.keySet()) {
            if (type.isSerializable()) {
                hooks.get(type).stream()
                    .filter(hook -> hook instanceof ISerializableHook)
                    .map(hook -> (ISerializableHook) hook)
                    .forEach(serializableHooks::add);
            }
        }
        return serializableHooks;
    }

    public HookMap merge(HookMap hookMap) {
        Builder builder = new Builder();

        for (Map.Entry<HookType<?>, IHook> entry : hooks.entries()) {
            builder.addHook(entry.getKey(), entry.getValue());
        }

        for (Map.Entry<HookType<?>, IHook> entry : hookMap.hooks.entries()) {
            builder.addHook(entry.getKey(), entry.getValue());
        }
        
        return builder.build();
    }


//    public static final StreamCodec<ByteBuf, HookMap> STREAM_CODEC = new StreamCodec<>() {
//        @Override
//        public void encode(@NotNull ByteBuf byteBuf, HookMap hookMap) {
//            Map<ResourceLocation, List<Integer>> result = new HashMap<>();
//            HookMapManager manager = HookMapManager.getInstance();
//
//            for (Map.Entry<HookType<?>, Collection<IHook>> entry : hookMap.hooks.asMap().entrySet()) {
//                HookType<?> type = entry.getKey();
//                Collection<IHook> hookList = entry.getValue();
//
//                ResourceLocation typeId = EBRegistries.Suppliers.HOOK_TYPES.get().getKey(type);
//
//                List<Integer> hookIds = new ArrayList<>();
//                for (IHook hook : hookList) {
//                    Integer id = manager.getIdForHook(hook);
//                    if (id != null) {
//                        hookIds.add(id);
//                    }
//                }
//
//                if (!hookIds.isEmpty()) {
//                    result.put(typeId, hookIds);
//                }
//            }
//
//            // 写入映射大小
//            FriendlyByteBuf buf = new FriendlyByteBuf(byteBuf);
//            buf.writeVarInt(result.size());
//
//            // 写入每个条目
//            for (Map.Entry<ResourceLocation, List<Integer>> entry : result.entrySet()) {
//                // 写入ResourceLocation
//                buf.writeResourceLocation(entry.getKey());
//
//                // 写入ID列表
//                List<Integer> ids = entry.getValue();
//                buf.writeVarInt(ids.size());
//                for (Integer id : ids) {
//                    buf.writeVarInt(id);
//                }
//            }
//        }
//
//        @Override
//        public @NotNull HookMap decode(@NotNull ByteBuf byteBuf) {
//            FriendlyByteBuf buf = new FriendlyByteBuf(byteBuf);
//            Builder builder = HookMap.builder();
//            HookMapManager manager = HookMapManager.getInstance();
//
//            // 读取映射大小
//            int size = buf.readVarInt();
//
//            // 读取每个条目
//            for (int i = 0; i < size; i++) {
//                // 读取ResourceLocation
//                ResourceLocation typeId = buf.readResourceLocation();
//
//                // 读取ID列表
//                int idsSize = buf.readVarInt();
//                List<Integer> hookIds = new ArrayList<>(idsSize);
//                for (int j = 0; j < idsSize; j++) {
//                    hookIds.add(buf.readVarInt());
//                }
//
//                HookType<?> type = EBRegistries.Suppliers.HOOK_TYPES.get().get(typeId);
//                if (type != null) {
//                    for (Integer hookId : hookIds) {
//                        IHook hook = manager.getHookById(hookId);
//                        if (hook != null) {
//                            builder.addHook(type, hook);
//                        }
//                    }
//                }
//            }
//
//            return builder.build();
//        }
//    };

    public static final Codec<HookMap> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            // 序列化为HookType的注册表ID和Hook的全局ID的映射
            Codec.unboundedMap(
                    ResourceLocation.CODEC, // HookType的注册表ID
                    Codec.list(Codec.INT) // Hook的全局ID列表
            ).fieldOf("hooks").forGetter(hookMap -> {
                Map<ResourceLocation, List<Integer>> result = new HashMap<>();
                HookMapManager manager = HookMapManager.getInstance();

                for (Map.Entry<HookType<?>, Collection<IHook>> entry : hookMap.hooks.asMap().entrySet()) {
                    HookType<?> type = entry.getKey();
                    Collection<IHook> hookList = entry.getValue();

                    ResourceLocation typeId = EBRegistries.Suppliers.HOOK_TYPES.get().getKey(type);

                    List<Integer> hookIds = new ArrayList<>();
                    for (IHook hook : hookList) {
                        Integer id = manager.getIdForHook(hook);
                        if (id != null) {
                            hookIds.add(id);
                        }
                    }

                    if (!hookIds.isEmpty()) {
                        result.put(typeId, hookIds);
                    }
                }

                return result;
            })
    ).apply(instance, map -> {
        Builder builder = HookMap.builder();
        HookMapManager manager = HookMapManager.getInstance();

        for (Map.Entry<ResourceLocation, List<Integer>> entry : map.entrySet()) {
            ResourceLocation typeId = entry.getKey();
            List<Integer> hookIds = entry.getValue();

            HookType<?> type = EBRegistries.Suppliers.HOOK_TYPES.get().get(typeId);
            if (type != null) {
                for (Integer hookId : hookIds) {
                    IHook hook = manager.getHookById(hookId);
                    if (hook != null) {
                        builder.addHook(type, hook);
                    }
                }
            }
        }

        return builder.build();
    }));

    public static class Builder {
        private final Multimap<HookType<?>, IHook> hooks = HashMultimap.create();
        private final BiMap<Integer, IHook> hooksById = HashBiMap.create();
        private final AtomicInteger nextId = new AtomicInteger(1);

        public <T extends IHook> Builder addHook(HookType<T> type, T hook) {
            hooks.put(type, hook);
            if (!hooksById.inverse().containsKey(hook)) {
                int id = nextId.getAndIncrement();
                hooksById.put(id, hook);
            }
            return this;
        }

        public Builder addHook(HookType<?> type, IHook... hookCollection) {
            for (IHook hook : hookCollection) {
                hooks.put(type, hook);
                if (!hooksById.inverse().containsKey(hook)) {
                    int id = nextId.getAndIncrement();
                    hooksById.put(id, hook);
                }
            }
            return this;
        }

        public HookMap build() {
            return new HookMap(hooks, hooksById);
        }
    }
}
