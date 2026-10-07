package net.satisfy.foundation.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.foundation.util.ShapeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

public class DresserBlock extends LineConnectingBlock implements EntityBlock, SimpleWaterloggedBlock, ContainerSoundBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    private static final Map<LineConnectingType, VoxelShape> NORTH_SHAPES = Map.of(
            LineConnectingType.NONE, Shapes.or(
                    Shapes.box(0.0625, 0, 0.0625, 0.25, 0.1875, 0.25), Shapes.box(0.75, 0, 0.0625, 0.9375, 0.1875, 0.25),
                    Shapes.box(0.0625, 0, 0.75, 0.25, 0.1875, 0.9375), Shapes.box(0.75, 0, 0.75, 0.9375, 0.1875, 0.9375),
                    Shapes.box(0, 0.8125, 0, 1, 1, 1), Shapes.box(0.0625, 0.1875, 0.0625, 0.9375, 0.8125, 0.9375)),
            LineConnectingType.RIGHT, Shapes.or(
                    Shapes.box(0, 0.8125, 0, 1, 1, 1), Shapes.box(0, 0.1875, 0.0625, 0.9375, 0.8125, 0.9375),
                    Shapes.box(0.75, 0, 0.0625, 0.9375, 0.1875, 0.25), Shapes.box(0.75, 0, 0.75, 0.9375, 0.1875, 0.9375)),
            LineConnectingType.LEFT, Shapes.or(
                    Shapes.box(0, 0.8125, 0, 1, 1, 1), Shapes.box(0.0625, 0.1875, 0.0625, 1, 0.8125, 0.9375),
                    Shapes.box(0.0625, 0, 0.75, 0.25, 0.1875, 0.9375), Shapes.box(0.0625, 0, 0.0625, 0.25, 0.1875, 0.25)),
            LineConnectingType.MIDDLE, Shapes.or(
                    Shapes.box(0, 0.1875, 0.0625, 1, 0.8125, 0.9375), Shapes.box(0, 0.8125, 0, 1, 1, 1)));
    private static final Map<Direction, Map<LineConnectingType, VoxelShape>> SHAPES = new EnumMap<>(Direction.class);

    static {
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            Map<LineConnectingType, VoxelShape> rotated = new EnumMap<>(LineConnectingType.class);
            NORTH_SHAPES.forEach((type, shape) -> rotated.put(type, ShapeUtil.rotateShape(Direction.NORTH, direction, shape)));
            SHAPES.put(direction, rotated);
        }
    }

    private final Supplier<? extends BlockEntityType<? extends CabinetBlockEntity>> blockEntityType;
    @Nullable
    private final Supplier<SoundEvent> openSound;
    @Nullable
    private final Supplier<SoundEvent> closeSound;

    public DresserBlock(Properties properties, Supplier<? extends BlockEntityType<? extends CabinetBlockEntity>> blockEntityType) {
        this(properties, blockEntityType, null, null);
    }

    public DresserBlock(Properties properties, Supplier<? extends BlockEntityType<? extends CabinetBlockEntity>> blockEntityType, @Nullable Supplier<SoundEvent> openSound, @Nullable Supplier<SoundEvent> closeSound) {
        super(properties);
        this.blockEntityType = blockEntityType;
        this.openSound = openSound;
        this.closeSound = closeSound;
        registerDefaultState(defaultBlockState().setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(WATERLOGGED);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        return state == null ? null : state.setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);
    }

    @Override
    public LineConnectingType getType(BlockState state, BlockState left, BlockState right) {
        boolean leftConnects = isConnected(state, left);
        boolean rightConnects = isConnected(state, right);
        if (leftConnects && rightConnects) {
            return LineConnectingType.MIDDLE;
        }
        if (leftConnects) {
            return LineConnectingType.LEFT;
        }
        return rightConnects ? LineConnectingType.RIGHT : LineConnectingType.NONE;
    }

    private boolean isConnected(BlockState self, BlockState neighbour) {
        if (!neighbour.hasProperty(FACING) || neighbour.getValue(FACING) != self.getValue(FACING)) {
            return false;
        }
        return neighbour.is(this) || connectsTo(neighbour);
    }

    protected boolean connectsTo(BlockState neighbour) {
        return false;
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (level.getBlockEntity(pos) instanceof CabinetBlockEntity cabinet) {
            player.openMenu(cabinet);
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public void playSound(Level level, BlockPos pos, boolean open) {
        Supplier<SoundEvent> sound = open ? openSound : closeSound;
        if (sound != null) {
            level.playSound(null, pos, sound.get(), SoundSource.BLOCKS, 1.0F, 1.1F);
        }
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (level.getBlockEntity(pos) instanceof CabinetBlockEntity cabinet) {
            cabinet.recheckOpen();
        }
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.is(newState.getBlock())) {
            if (level.getBlockEntity(pos) instanceof CabinetBlockEntity cabinet) {
                Containers.dropContents(level, pos, cabinet);
                level.updateNeighbourForOutputSignal(pos, this);
            }
            super.onRemove(state, level, pos, newState, moved);
        }
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (stack.has(DataComponents.CUSTOM_NAME) && level.getBlockEntity(pos) instanceof CabinetBlockEntity cabinet) {
            cabinet.setComponents(DataComponentMap.builder().set(DataComponents.CUSTOM_NAME, stack.getHoverName()).build());
        }
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(level.getBlockEntity(pos));
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
    protected @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES.get(state.getValue(FACING)).get(state.getValue(TYPE));
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return blockEntityType.get().create(pos, state);
    }
}
