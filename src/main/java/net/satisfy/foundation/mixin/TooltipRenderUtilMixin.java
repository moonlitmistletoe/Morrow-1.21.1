package net.satisfy.foundation.mixin;

import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.satisfy.foundation.tooltip.TooltipBorder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/** Swaps the tooltip border colors when {@link TooltipBorder} was marked just before. */
@Mixin(TooltipRenderUtil.class)
public abstract class TooltipRenderUtilMixin {
    @ModifyVariable(method = "renderFrameGradient", at = @At("HEAD"), argsOnly = true, ordinal = 5, require = 0)
    private static int foundation$borderTop(int color) {
        return TooltipBorder.top(color);
    }

    @ModifyVariable(method = "renderFrameGradient", at = @At("HEAD"), argsOnly = true, ordinal = 6, require = 0)
    private static int foundation$borderBottom(int color) {
        return TooltipBorder.bottom(color);
    }
}
