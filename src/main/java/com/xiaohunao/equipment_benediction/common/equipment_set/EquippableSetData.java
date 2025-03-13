package com.xiaohunao.equipment_benediction.common.equipment_set;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.xiaohunao.equipment_benediction.common.equippable.IEquippable;
import com.xiaohunao.equipment_benediction.common.hook.HookMap;
import com.xiaohunao.equipment_benediction.common.hook.HookType;
import com.xiaohunao.equipment_benediction.common.hook.IHook;
import com.xiaohunao.equipment_benediction.common.init.EBHookTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.units.qual.C;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public final class EquippableSetData {

    public final static Codec<EquippableSetData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(IEquippable.CODEC, Ingredient.CODEC).fieldOf("equipages").forGetter(EquippableSetData::equipages),
            Codec.list(IEquippable.CODEC).optionalFieldOf("blacklist", Lists.newArrayList()).forGetter(EquippableSetData::blacklist),
            Codec.INT.optionalFieldOf("required_match_count").forGetter(EquippableSetData::getRequiredMatchCount),
            HookMap.CODEC.optionalFieldOf("hooks", new HookMap.Builder().build()).forGetter(EquippableSetData::getHookMap)
    ).apply(instance, EquippableSetData::new));
    private final Map<IEquippable, Ingredient> equipages;
    private final List<IEquippable> blacklist;
    private final Optional<Integer> requiredMatchCount;
    private final HookMap hookMap;

    public EquippableSetData(Map<IEquippable, Ingredient> equipages, List<IEquippable> blacklist,
                             Optional<Integer> requiredMatchCount, HookMap hookMap) {
        this.equipages = equipages;
        this.blacklist = blacklist;
        this.requiredMatchCount = requiredMatchCount;
        this.hookMap = hookMap;
    }


    public Optional<Integer> getRequiredMatchCount() {
        return requiredMatchCount;
    }

    public boolean isValid(LivingEntity livingEntity) {
        boolean match = requiredMatchCount.map(integer -> equipages.entrySet()
                        .stream()
                        .filter(entry -> entry.getKey().checkEquippable(livingEntity, entry.getValue()))
                        .count() >= integer)
                .orElseGet(() -> equipages.entrySet()
                        .stream()
                        .allMatch(entry -> entry.getKey().checkEquippable(livingEntity, entry.getValue()))
                );

        boolean black = blacklist.stream()
                .allMatch(equippable -> equippable.checkEquippable(livingEntity, Ingredient.EMPTY));

        return match && black;
    }

    public Map<IEquippable, Ingredient> equipages() {
        return equipages;
    }

    public List<IEquippable> blacklist() {
        return blacklist;
    }

    public HookMap getHookMap() {
        return hookMap;
    }

    public Optional<Integer> requiredMatchCount() {
        return requiredMatchCount;
    }

    public HookMap hookMap() {
        return hookMap;
    }


    public static class Builder {
        private final Map<IEquippable, Ingredient> equipages = Maps.newHashMap();
        private final List<IEquippable> blacklist = Lists.newArrayList();
        private final HookMap.Builder hookMap = new HookMap.Builder();
        private Integer requiredMatchCount;

        public Builder addEquippable(IEquippable equippable, Ingredient ingredient) {
            if (equippable == null || ingredient == null) {
                throw new IllegalArgumentException("Equippable and Ingredient cannot be null");
            }
            equipages.put(equippable, ingredient);
            return this;
        }

        public Builder addEquippable(Object... equipPairs) {
            if (equipPairs == null || equipPairs.length == 0) {
                throw new IllegalArgumentException("EquipmentSet pairs cannot be null or empty");
            }
            if (equipPairs.length % 2 != 0) {
                throw new IllegalArgumentException("EquipmentSet pairs must be in pairs");
            }

            for (int i = 0; i < equipPairs.length; i += 2) {
                if (!(equipPairs[i] instanceof IEquippable equippable) || !(equipPairs[i + 1] instanceof Ingredient ingredient)) {
                    throw new IllegalArgumentException(
                            "Invalid pair type at index " + i +
                                    ". Expected IEquippable and Ingredient"
                    );
                }
                equipages.put(equippable, ingredient);
            }
            return this;
        }

        public Builder setRequiredMatchCount(int requiredMatchCount) {
            this.requiredMatchCount = requiredMatchCount;
            return this;
        }


        public Builder addBlacklist(IEquippable equippable) {
            if (equippable == null) {
                throw new IllegalArgumentException("Equippable cannot be null");
            }
            this.blacklist.add(equippable);
            return this;
        }

        public <T extends IHook> Builder bindHook(HookType<T> type, T hook) {
            hookMap.addHook(type, hook);
            return this;
        }

        public Builder bindHook(Function<WearBonus.Builder, WearBonus.Builder> wearBonusHook) {
            WearBonus wearBonus = wearBonusHook.apply(new WearBonus.Builder()).build();

            if (!wearBonus.attributes.isEmpty() || !wearBonus.mobEffectInstances.isEmpty()) {
                hookMap.addHook(EBHookTypes.EQUIP_EQUIPMENT.get(), wearBonus);
                hookMap.addHook(EBHookTypes.UNEQUIP_EQUIPMENT.get(), wearBonus);
            }

            if (!wearBonus.mobEffects.isEmpty()) {
                hookMap.addHook(EBHookTypes.MOB_EFFECT_APPLICABLE.get(), wearBonus);
            }

            if (!wearBonus.damageTypes.isEmpty()) {
                hookMap.addHook(EBHookTypes.LIVING_INCOMING_DAMAGE.get(), wearBonus);
            }
            return this;
        }


        public EquippableSetData build() {
            return new EquippableSetData(equipages, blacklist, Optional.ofNullable(requiredMatchCount), hookMap.build());
        }

    }
}