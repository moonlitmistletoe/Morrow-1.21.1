package net.satisfy.foundation.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.foundation.registry.FoundationTags;

import java.util.function.Supplier;

/**
 * Cake with 4 pieces. Cut it with anything in {@code foundation:cake_cutters} to get
 * a slice item, or sneak + empty hand to eat a piece directly. Empty hand on a
 * untouched cake picks it up again.
 */
public class SliceableCakeBlock extends Block {
    public static final int MAX_CUTS = 3;
    public static final IntegerProperty CUTS = IntegerProperty.create("cuts", 0, MAX_CUTS);
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    private static final double HEIGHT = 5;
    private static final double[][] QUARTERS_NORTH = {{2, 2, 8, 8}, {8, 2, 14, 8}, {2, 8, 8, 14}, {8, 8, 14, 14}};

    private final Supplier<? extends ItemLike> slice;

    /** @param slice the slice item, its food component is also used when eating from the block */
    public SliceableCakeBlock(Properties properties, Supplier<? extends ItemLike> slice) {
        super(properties);
        this.slice = slice;
        registerDefaultState(stateDefinition.any().setValue(CUTS, 0).setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(CUTS, FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        VoxelShape shape = Shapes.empty();
        int turns = state.getValue(FACING).get2DDataValue() - Direction.NORTH.get2DDataValue();
        for (int i = state.getValue(CUTS); i < QUARTERS_NORTH.length; i++) {
            double[] quarter = QUARTERS_NORTH[i];
            double minX = quarter[0];
            double minZ = quarter[1];
            double maxX = quarter[2];
            double maxZ = quarter[3];
            for (int turn = 0; turn < Math.floorMod(turns, 4); turn++) {
                double rotatedMinX = 16 - maxZ;
                double rotatedMaxX = 16 - minZ;
                minZ = minX;
                maxZ = maxX;
                minX = rotatedMinX;
                maxX = rotatedMaxX;
            }
            shape = Shapes.or(shape, Block.box(minX, 0, minZ, maxX, HEIGHT, maxZ));
        }
        return shape;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (player.isShiftKeyDown() && stack.isEmpty()) {
            return eat(level, pos, state, player);
        }
        if (stack.is(FoundationTags.CAKE_CUTTERS)) {
            if (!level.isClientSide) {
                removePiece(level, pos, state);
                drop(level, pos, player, new ItemStack(slice.get()));
                level.playSound(null, pos, SoundEvents.WOOL_BREAK, SoundSource.PLAYERS, 0.75F, 1.2F);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        if (stack.isEmpty() && state.getValue(CUTS) == 0) {
            if (!level.isClientSide) {
                level.removeBlock(pos, false);
                drop(level, pos, player, new ItemStack(this));
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    private ItemInteractionResult eat(Level level, BlockPos pos, BlockState state, Player player) {
        if (!player.canEat(false)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (!level.isClientSide) {
            FoodProperties food = new ItemStack(slice.get()).get(DataComponents.FOOD);
            if (food != null) {
                player.getFoodData().eat(food.nutrition(), food.saturation());
                food.effects().forEach(effect -> player.addEffect(new MobEffectInstance(effect.effect())));
            }
            removePiece(level, pos, state);
            level.playSound(null, pos, SoundEvents.GENERIC_EAT, SoundSource.PLAYERS, 0.8F, 0.8F);
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }

    private static void removePiece(Level level, BlockPos pos, BlockState state) {
        int cuts = state.getValue(CUTS);
        if (cuts < MAX_CUTS) {
            level.setBlock(pos, state.setValue(CUTS, cuts + 1), Block.UPDATE_ALL);
        } else {
            level.removeBlock(pos, false);
        }
    }

    private static void drop(Level level, BlockPos pos, Player player, ItemStack stack) {
        Direction direction = player.getDirection().getOpposite();
        ItemEntity item = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.4, pos.getZ() + 0.5, stack, direction.getStepX() * 0.13, 0.35, direction.getStepZ() * 0.13);
        level.addFreshEntity(item);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighbor, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        return direction == Direction.DOWN && !state.canSurvive(level, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, direction, neighbor, level, pos, neighborPos);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return level.getBlockState(pos.below()).isSolid();
    }
}
