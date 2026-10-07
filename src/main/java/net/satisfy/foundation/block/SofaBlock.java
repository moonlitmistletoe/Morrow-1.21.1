package net.satisfy.foundation.block;

import net.satisfy.foundation.util.DyeHelper;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.foundation.seat.SeatUtil;
import org.jetbrains.annotations.NotNull;

public class SofaBlock extends LineConnectingBlock {
    public static final EnumProperty<ClothColor> COLOR = EnumProperty.create("color", ClothColor.class);

    private final double seatHeight;

    public SofaBlock(Properties properties) {
        this(properties, 0.2);
    }

    public SofaBlock(Properties properties, double seatHeight) {
        super(properties);
        this.seatHeight = seatHeight;
        registerDefaultState(defaultBlockState().setValue(COLOR, ClothColor.NONE));
    }

    protected VoxelShape getSofaShape(LineConnectingType type, Direction facing) {
        return Shapes.block();
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getSofaShape(state.getValue(TYPE), state.getValue(FACING));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(COLOR);
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (stack.getItem() instanceof DyeItem dye && state.getValue(COLOR).color() != dye.getDyeColor()) {
            return DyeHelper.apply(stack, dye.getDyeColor(), state.setValue(COLOR, ClothColor.of(dye.getDyeColor())), level, pos, player);
        }
        return SeatUtil.onUse(level, player, hand, hit, seatHeight);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
        SeatUtil.onStateReplaced(level, pos);
        super.onRemove(state, level, pos, newState, moved);
    }
}
