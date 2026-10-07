package net.satisfy.foundation.entity.ai;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.util.RandomSource;
import net.satisfy.foundation.Foundation;

import java.util.EnumSet;

public class RandomActionGoal extends Goal {
    private static final AttributeModifier FREEZE = new AttributeModifier(Foundation.identifier("random_action_freeze"), -1000, AttributeModifier.Operation.ADD_VALUE);
    private final LivingEntity mob;
    private final RandomAction action;
    private final RandomSource random = RandomSource.create();
    private int counter;

    public <T extends LivingEntity & RandomAction> RandomActionGoal(T mob) {
        this.mob = mob;
        this.action = mob;
        setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE, Flag.JUMP));
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public boolean isInterruptable() {
        return action.isInterruptable();
    }

    @Override
    public boolean canUse() {
        return random.nextFloat() < action.chance() && action.isPossible();
    }

    @Override
    public boolean canContinueToUse() {
        return counter > 0 && counter < action.duration() && action.isPossible();
    }

    @Override
    public void start() {
        counter = 0;
        action.onStart();
        AttributeInstance speed = mob.getAttribute(Attributes.MOVEMENT_SPEED);
        if (!action.canMove() && speed != null && !speed.hasModifier(FREEZE.id())) {
            speed.addTransientModifier(FREEZE);
        }
    }

    @Override
    public void tick() {
        counter++;
        action.onTick(counter);
    }

    @Override
    public void stop() {
        action.onStop();
        AttributeInstance speed = mob.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed != null) {
            speed.removeModifier(FREEZE.id());
        }
    }
}
