package net.satisfy.foundation.entity.ai;

import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

public interface AttackAnimationMob {
    @Nullable
    LivingEntity getAttackTarget();

    void setAttacking(boolean attacking);

    void performAttack(LivingEntity target);
}
