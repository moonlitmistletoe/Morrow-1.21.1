package net.moonlitmistletoe.morrow.block;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.core.BlockPos;

public class StrawberryCropBlock extends CropBlock {

    public StrawberryCropBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(BlockTags.DIRT) || state.is(net.minecraft.world.level.block.Blocks.FARMLAND);
    }
}