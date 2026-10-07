package net.satisfy.foundation.overlay;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/** Supplies the content of the {@link BlockInfoOverlay} for a block. */
public interface BlockInfoProvider {
    /**
     * @param hit null if the block is tracked but not targeted
     * @return sections to show, empty list = not my block
     */
    List<InfoSection> describe(Level level, BlockPos pos, BlockState state, @Nullable BlockHitResult hit);

    /** Hook right before the background gets drawn, e.g. to mark a {@link net.satisfy.foundation.tooltip.TooltipBorder}. */
    default void beforeBackground(Level level, BlockPos pos, BlockState state) {
    }
}
