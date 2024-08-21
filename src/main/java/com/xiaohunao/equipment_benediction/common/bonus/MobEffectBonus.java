package com.xiaohunao.equipment_benediction.common.bonus;

import com.xiaohunao.equipment_benediction.api.IBonus;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;

public class MobEffectBonus implements IBonus {
    private MobEffect mobEffect;
    private MobEffectInstance mobEffectInstance;
    private int duration;

    public MobEffectBonus(MobEffect effect, MobEffectInstance mobEffectInstance) {
        this.mobEffect = effect;
        this.mobEffectInstance = mobEffectInstance;
        this.duration = mobEffectInstance.getDuration();
    }

    @Override
    public void apply(Player player) {
        player.addEffect(new MobEffectInstance(mobEffectInstance),player);
    }

    @Override
    public void clear(Player player) {
        player.removeEffect(mobEffect);
    }
}
