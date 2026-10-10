package net.moonlitmistletoe.morrow.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class JamBlock extends HorizontalDirectionalBlock {

    public static final MapCodec<JamBlock> CODEC =
            simpleCodec(JamBlock::new);

    public static final IntegerProperty STACK =
            IntegerProperty.create("stack", 1, 8);

    public JamBlock(Properties properties) {
        super(properties.noOcclusion());

        this.registerDefaultState(
                this.defaultBlockState()
                        .setValue(FACING, Direction.NORTH)
                        .setValue(STACK, 1)
        );
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(
            StateDefinition.Builder<Block, BlockState> builder
    ) {
        builder.add(FACING, STACK);
    }

    @Override
    public BlockState getStateForPlacement(
            BlockPlaceContext context
    ) {
        return this.defaultBlockState()
                .setValue(
                        FACING,
                        context.getHorizontalDirection().getOpposite()
                )
                .setValue(STACK, 1);
    }
}