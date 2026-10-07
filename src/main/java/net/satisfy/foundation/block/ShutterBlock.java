package net.satisfy.foundation.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class ShutterBlock extends Block implements SimpleWaterloggedBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<VerticalConnectingType> TYPE = EnumProperty.create("type", VerticalConnectingType.class);
    public static final BooleanProperty LEFT = BooleanProperty.create("left");
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private static final VoxelShape[] SHAPES = {
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 3.0D),
            Block.box(13.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D),
            Block.box(0.0D, 0.0D, 13.0D, 16.0D, 16.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 3.0D, 16.0D, 16.0D)
    };

    public ShutterBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(TYPE, VerticalConnectingType.NONE).setValue(OPEN, false).setValue(LEFT, false).setValue(POWERED, false).setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, TYPE, OPEN, LEFT, POWERED, WATERLOGGED);
    }

    @Override
    protected @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        int index = state.getValue(FACING).get2DDataValue() + (state.getValue(OPEN) ? (state.getValue(LEFT) ? 3 : 1) : 0);
        return SHAPES[index % 4];
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection().getOpposite();
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Vec3 click = context.getClickLocation();

        boolean left = facing.getAxis() == Direction.Axis.X ? click.z - pos.getZ() > 0.5D : click.x - pos.getX() > 0.5D;
        if (context.getNearestLookingDirection() == Direction.NORTH || context.getNearestLookingDirection() == Direction.EAST) {
            left = !left;
        }
        BlockState state = defaultBlockState().setValue(FACING, facing).setValue(LEFT, left);
        if (level.hasNeighborSignal(pos)) {
            state = state.setValue(OPEN, true).setValue(POWERED, true);
        }
        state = state.setValue(TYPE, getType(state, level.getBlockState(pos.above()), level.getBlockState(pos.below())));
        return state.setValue(WATERLOGGED, level.getFluidState(pos).getType() == Fluids.WATER);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (level.isClientSide) {
            return;
        }
        BlockState updated = state;
        boolean powered = level.hasNeighborSignal(pos);
        if (powered != state.getValue(POWERED)) {
            if (state.getValue(OPEN) != powered) {
                updated = updated.setValue(OPEN, powered);
                level.playSound(null, pos, getSound(powered), SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            updated = updated.setValue(POWERED, powered);
        }
        updated = updated.setValue(TYPE, getType(updated, level.getBlockState(pos.above()), level.getBlockState(pos.below())));
        if (updated != state) {
            level.setBlock(pos, updated, 3);
        }
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        state = state.cycle(OPEN);
        level.setBlock(pos, state, 3);
        if (!player.isCrouching()) {
            toggleConnected(state, level, pos, state.getValue(OPEN));
        }
        level.playSound(null, pos, getSound(state.getValue(OPEN)), SoundSource.BLOCKS, 1.0F, 1.0F);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    public void toggleConnected(BlockState state, Level level, BlockPos pos, boolean open) {
        VerticalConnectingType type = state.getValue(TYPE);
        if (type == VerticalConnectingType.MIDDLE || type == VerticalConnectingType.BOTTOM) {
            toggleLine(state, level, pos, Direction.UP, open);
        }
        if (type == VerticalConnectingType.MIDDLE || type == VerticalConnectingType.TOP) {
            toggleLine(state, level, pos, Direction.DOWN, open);
        }
    }

    private void toggleLine(BlockState state, Level level, BlockPos pos, Direction direction, boolean open) {
        BlockPos current = pos.relative(direction);
        while (!level.isOutsideBuildHeight(current)) {
            BlockState other = level.getBlockState(current);
            if (!other.is(this) || other.getValue(FACING) != state.getValue(FACING) || other.getValue(LEFT) != state.getValue(LEFT) || other.getValue(OPEN) == open) {
                return;
            }
            level.setBlock(current, other.setValue(OPEN, open), 3);
            current = current.relative(direction);
        }
    }

    protected SoundEvent getSound(boolean open) {
        return open ? SoundEvents.BAMBOO_WOOD_DOOR_OPEN : SoundEvents.BAMBOO_WOOD_DOOR_CLOSE;
    }

    public VerticalConnectingType getType(BlockState state, BlockState above, BlockState below) {
        boolean aboveSame = isSameShutter(state, above);
        boolean belowSame = isSameShutter(state, below);
        if (aboveSame && belowSame) {
            return VerticalConnectingType.MIDDLE;
        }
        if (aboveSame) {
            return VerticalConnectingType.BOTTOM;
        }
        return belowSame ? VerticalConnectingType.TOP : VerticalConnectingType.NONE;
    }

    private boolean isSameShutter(BlockState state, BlockState other) {
        return other.is(this) && other.getValue(FACING) == state.getValue(FACING) && other.getValue(OPEN) == state.getValue(OPEN) && other.getValue(LEFT) == state.getValue(LEFT);
    }

    @Override
    protected @NotNull BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    protected @NotNull FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected @NotNull BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected @NotNull BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }
}
