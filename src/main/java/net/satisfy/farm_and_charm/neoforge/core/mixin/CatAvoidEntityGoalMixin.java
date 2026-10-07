package net.satisfy.farm_and_charm.neoforge.core.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.world.entity.ai.goal.AvoidEntityGoal")
public class CatAvoidEntityGoalMixin {
    @Shadow private LivingEntity toAvoid;

    @Inject(method = "canUse", at = @At("HEAD"), cancellable = true)
    private void modifyCanUse(CallbackInfoReturnable<Boolean> cir) {
        if (toAvoid instanceof net.minecraft.world.entity.player.Player p && p.isHolding(ObjectRegistry.CAT_FOOD.get().asItem()))
            cir.setReturnValue(false);
    }

    @Inject(method = "canContinueToUse", at = @At("HEAD"), cancellable = true)
    private void modifyCanContinueToUse(CallbackInfoReturnable<Boolean> cir) {
        if (toAvoid instanceof net.minecraft.world.entity.player.Player p && p.isHolding(ObjectRegistry.CAT_FOOD.get().asItem()))
            cir.setReturnValue(false);
    }
}
