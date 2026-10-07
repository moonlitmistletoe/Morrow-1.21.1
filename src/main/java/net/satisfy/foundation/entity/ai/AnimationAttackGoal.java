package net.satisfy.foundation.entity.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

public class AnimationAttackGoal extends MeleeAttackGoal {
    private static final int TIMEOUT = 20 * 3;
    private final AttackAnimationMob animated;
    private final int attackDelay;
    private final int attackTick;
    private int counter;
    private int timeout;

    public <T extends PathfinderMob & AttackAnimationMob> AnimationAttackGoal(T mob, double speedModifier, boolean followingTargetEvenIfNotSeen, int attackDelay, int attackTick) {
        super(mob, speedModifier, followingTargetEvenIfNotSeen);
        this.animated = mob;
        this.attackDelay = attackDelay;
        this.attackTick = attackTick;
    }

    @Override
    public void start() {
        timeout = 0;
        super.start();
    }

    @Override
    public boolean canContinueToUse() {
        return super.canContinueToUse() && timeout < TIMEOUT;
    }

    @Override
    public void tick() {
        super.tick();
        animated.setAttacking(counter != 0);
        if (counter != 0) {
            counter++;
        }
        if (counter >= attackDelay) {
            counter = 0;
        }
    }

    @Override
    protected void checkAndPerformAttack(LivingEntity target) {
        if (isTimeToAttack() && mob.isWithinMeleeAttackRange(target) && mob.getSensing().hasLineOfSight(target)) {
            if (counter == 0) {
                counter++;
            }
            if (counter == attackTick) {
                animated.performAttack(target);
            }
            timeout = 0;
        } else {
            timeout++;
        }
    }

    @Override
    public void stop() {
        animated.setAttacking(false);
        counter = 0;
        super.stop();
    }
}
