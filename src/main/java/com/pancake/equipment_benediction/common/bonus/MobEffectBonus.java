package com.pancake.equipment_benediction.common.bonus;

import com.pancake.equipment_benediction.api.IBonus;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;

public class MobEffectBonus implements IBonus {
    private MobEffect mobEffect;
    private MobEffectInstance mobEffectInstance;

    public MobEffectBonus(MobEffect effect, MobEffectInstance mobEffectInstance) {
        this.mobEffect = effect;
        this.mobEffectInstance = mobEffectInstance;
    }

    @Override
    public void apply(Player player) {
        player.addEffect(mobEffectInstance);
    }

    @Override
    public void clear(Player player) {
        player.removeEffect(mobEffect);
    }
}
