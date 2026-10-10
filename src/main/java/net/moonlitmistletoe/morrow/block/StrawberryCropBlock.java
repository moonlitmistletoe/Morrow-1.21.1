package net.moonlitmistletoe.morrow.block;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.moonlitmistletoe.morrow.item.ModItems;

public class StrawberryCropBlock extends CropBlock {

    public StrawberryCropBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected Item getBaseSeedId() {
        return ModItems.STRAWBERRY_SEED.get();
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(BlockTags.DIRT) || state.is(net.minecraft.world.level.block.Blocks.FARMLAND);
    }
}