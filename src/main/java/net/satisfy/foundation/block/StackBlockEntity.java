package net.satisfy.foundation.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.satisfy.foundation.food.FoodBlock;
import net.satisfy.foundation.registry.FoundationBlockEntities;

public class StackBlockEntity extends BlockEntity {
    private long wobbleStart = Long.MIN_VALUE / 2;

    public StackBlockEntity(BlockPos pos, BlockState state) {
        super(FoundationBlockEntities.STACK.get(), pos, state);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void setBlockState(BlockState state) {
        BlockState previous = getBlockState();
        super.setBlockState(state);
        if (level != null && level.isClientSide() && (changed(previous, state, StackableBlock.STACK_PROPERTY) || changed(previous, state, FoodBlock.BITES))) {
            wobbleStart = level.getGameTime();
        }
    }

    private static boolean changed(BlockState previous, BlockState state, IntegerProperty property) {
        return previous.hasProperty(property) && state.hasProperty(property) && !previous.getValue(property).equals(state.getValue(property));
    }

    public long getWobbleStart() {
        return wobbleStart;
    }
}
