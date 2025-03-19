package com.xiaohunao.equipment_benediction.common.context;

import java.util.Optional;

public record DamageResultContainer(Optional<Boolean> isCanceled, Optional<Float> damage,Optional<Integer> InvulnerabilityTick) {

    public static DamageResultContainer empty() {
        return new DamageResultContainer(Optional.empty(), Optional.empty(), Optional.empty());
    }

    public static DamageResultContainer invulnerability(int invulnerabilityTick) {
        return new DamageResultContainer(Optional.empty(), Optional.empty(), Optional.of(invulnerabilityTick));
    }

    public static DamageResultContainer damage(float damage) {
        return new DamageResultContainer(Optional.empty(), Optional.of(damage), Optional.empty());
    }

    public static DamageResultContainer cancel(boolean canceled) {
        return new DamageResultContainer(Optional.of(canceled), Optional.empty(), Optional.empty());
    }
}
