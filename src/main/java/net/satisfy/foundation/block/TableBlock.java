package net.satisfy.foundation.block;

import net.satisfy.foundation.util.DyeHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.Containers;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.block.WoolCarpetBlock;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class TableBlock extends LineConnectingBlock implements SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final EnumProperty<ClothColor> TABLECLOTH = EnumProperty.create("tablecloth", ClothColor.class);
    private static final VoxelShape TOP_SHAPE = box(0, 13, 0, 16, 16, 16);
    private static final VoxelShape[] LEG_SHAPES = {
            box(1, 0, 1, 4, 13, 4),
            box(12, 0, 1, 15, 13, 4),
            box(12, 0, 12, 15, 13, 15),
            box(1, 0, 12, 4, 13, 15)
    };

    private final boolean alwaysCovered;

    public TableBlock(Properties properties) {
        this(properties, ClothColor.NONE);
    }

    public TableBlock(Properties properties, ClothColor fixedCloth) {
        super(properties);
        this.alwaysCovered = fixedCloth != ClothColor.NONE;
        registerDefaultState(defaultBlockState().setValue(WATERLOGGED, false).setValue(TABLECLOTH, fixedCloth));
    }

    protected VoxelShape getTableShape(LineConnectingType type, Direction facing) {
        if (type == LineConnectingType.MIDDLE) {
            return TOP_SHAPE;
        }
        boolean leftEnd = facing == Direction.NORTH && type == LineConnectingType.LEFT || facing == Direction.SOUTH && type == LineConnectingType.RIGHT;
        boolean rightEnd = facing == Direction.NORTH && type == LineConnectingType.RIGHT || facing == Direction.SOUTH && type == LineConnectingType.LEFT;
        boolean eastLeft = facing == Direction.EAST && type == LineConnectingType.LEFT || facing == Direction.WEST && type == LineConnectingType.RIGHT;
        boolean eastRight = facing == Direction.EAST && type == LineConnectingType.RIGHT || facing == Direction.WEST && type == LineConnectingType.LEFT;
        if (leftEnd) return Shapes.or(TOP_SHAPE, LEG_SHAPES[0], LEG_SHAPES[3]);
        if (rightEnd) return Shapes.or(TOP_SHAPE, LEG_SHAPES[1], LEG_SHAPES[2]);
        if (eastLeft) return Shapes.or(TOP_SHAPE, LEG_SHAPES[0], LEG_SHAPES[1]);
        if (eastRight) return Shapes.or(TOP_SHAPE, LEG_SHAPES[2], LEG_SHAPES[3]);
        return Shapes.or(TOP_SHAPE, LEG_SHAPES);
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getTableShape(state.getValue(TYPE), state.getValue(FACING));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        return state == null ? null : state.setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ClothColor cloth = state.getValue(TABLECLOTH);
        if (!alwaysCovered && cloth == ClothColor.NONE && Block.byItem(stack.getItem()) instanceof WoolCarpetBlock carpet) {
            if (!level.isClientSide()) {
                level.setBlockAndUpdate(pos, state.setValue(TABLECLOTH, ClothColor.of(carpet.getColor())));
                level.playSound(null, pos, SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
                stack.consume(1, player);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide());
        }
        if (cloth != ClothColor.NONE && stack.getItem() instanceof DyeItem dye && cloth.color() != dye.getDyeColor()) {
            return DyeHelper.apply(stack, dye.getDyeColor(), state.setValue(TABLECLOTH, ClothColor.of(dye.getDyeColor())), level, pos, player);
        }
        if (!alwaysCovered && cloth != ClothColor.NONE && stack.isEmpty() && player.isShiftKeyDown()) {
            if (!level.isClientSide()) {
                level.setBlockAndUpdate(pos, state.setValue(TABLECLOTH, ClothColor.NONE));
                level.playSound(null, pos, SoundEvents.WOOL_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
                ItemStack carpet = new ItemStack(carpetFor(cloth.color()));
                if (!player.getInventory().add(carpet)) player.drop(carpet, false);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide());
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hit);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
        ClothColor cloth = state.getValue(TABLECLOTH);
        if (!alwaysCovered && !state.is(newState.getBlock()) && cloth != ClothColor.NONE) {
            Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(carpetFor(cloth.color())));
        }
        super.onRemove(state, level, pos, newState, moved);
    }

    private static Item carpetFor(DyeColor color) {
        for (Block block : BuiltInRegistries.BLOCK) {
            if (block instanceof WoolCarpetBlock carpet && carpet.getColor() == color) return block.asItem();
        }
        return Items.WHITE_CARPET;
    }

    @Override
    public @NotNull BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(WATERLOGGED, TABLECLOTH);
    }

    @Override
    public @NotNull FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }
}
