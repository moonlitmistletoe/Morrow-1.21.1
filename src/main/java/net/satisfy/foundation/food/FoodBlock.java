package net.satisfy.foundation.food;

import net.satisfy.foundation.block.FacingBlock;
import net.satisfy.foundation.block.StackBlockEntity;
import net.satisfy.foundation.block.StackableBlock;
import net.satisfy.foundation.util.ShapeUtil;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

/**
 * Placeable food (only when sneaking). Each click eats one bite until
 * {@code maxBites} is reached, then it is gone.
 */
public class FoodBlock extends FacingBlock implements EntityBlock {
    public static final DirectionProperty FACING;
    public static final IntegerProperty BITES;
    public static final BooleanProperty ANIMATED = StackableBlock.ANIMATED;
    private final int maxBites;
    private final FoodProperties foodComponent;
    private final VoxelShape SHAPE = Shapes.box(0.1875, 0, 0.1875, 0.8125, 0.875, 0.8125);
    private final VoxelShape[][] shapes;

    public FoodBlock(Properties settings, int maxBites, FoodProperties foodComponent) {
        super(settings);
        this.maxBites = maxBites;
        this.foodComponent = foodComponent;
        this.shapes = null;
        registerDefaultState(this.defaultBlockState().setValue(BITES, 0).setValue(FACING, Direction.NORTH).setValue(ANIMATED, false));
    }

    public FoodBlock(Properties settings, int maxBites, FoodProperties foodComponent, VoxelShape... shapesNorthByBites) {
        super(settings);
        this.maxBites = maxBites;
        this.foodComponent = foodComponent;
        this.shapes = new VoxelShape[shapesNorthByBites.length][4];
        for (int bites = 0; bites < shapesNorthByBites.length; bites++) {
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                this.shapes[bites][direction.get2DDataValue()] = ShapeUtil.rotateShape(Direction.NORTH, direction, shapesNorthByBites[bites]);
            }
        }
        registerDefaultState(this.defaultBlockState().setValue(BITES, 0).setValue(FACING, Direction.NORTH).setValue(ANIMATED, false));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        if (!Objects.requireNonNull(ctx.getPlayer()).isShiftKeyDown()) {
            return null;
        }
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite()).setValue(ANIMATED, true);
    }

    @Override
    public @NotNull InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        if (world.isClientSide) {
            return InteractionResult.CONSUME;
        }
        return tryEat(world, pos, state, player);
    }

    private InteractionResult tryEat(LevelAccessor world, BlockPos pos, BlockState state, Player player) {
        ItemStack stack = new ItemStack(asItem());
        FoodProperties fp = stack.get(DataComponents.FOOD);
        if (fp == null) fp = this.foodComponent;

        player.getFoodData().eat(fp.nutrition(), fp.saturation());

        if (world instanceof Level level) {
            for (FoodProperties.PossibleEffect effect : fp.effects()) {
                if (effect.probability() >= 1.0F || level.random.nextFloat() < effect.probability()) {
                    player.addEffect(new MobEffectInstance(effect.effect()));
                }
            }
        }

        world.playSound(null, pos, SoundEvents.GENERIC_EAT, SoundSource.PLAYERS, 0.5f, world.getRandom().nextFloat() * 0.1f + 0.9f);
        world.gameEvent(player, GameEvent.EAT, pos);

        if (world instanceof Level level) {
            for (int i = 0; i < 10; ++i) {
                double dx = level.random.nextGaussian() * 0.02D;
                double dy = level.random.nextGaussian() * 0.02D;
                double dz = level.random.nextGaussian() * 0.02D;
                level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, state), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, dx, dy, dz);
            }
        }

        int bites = state.getValue(BITES);
        if (bites < maxBites - 1) {
            world.setBlock(pos, state.setValue(BITES, bites + 1).setValue(ANIMATED, true), 3);
        } else {
            world.destroyBlock(pos, false);
            world.gameEvent(player, GameEvent.BLOCK_DESTROY, pos);
        }

        return InteractionResult.SUCCESS;
    }

    public @NotNull BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    public @NotNull BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, BITES, ANIMATED);
    }

    static {
        FACING = BlockStateProperties.HORIZONTAL_FACING;
        BITES = IntegerProperty.create("bites", 0, 9);
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        if (shapes == null || shapes.length == 0) {
            return SHAPE;
        }
        return shapes[Math.min(state.getValue(BITES), shapes.length - 1)][state.getValue(FACING).get2DDataValue()];
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return state.getValue(ANIMATED) ? RenderShape.ENTITYBLOCK_ANIMATED : RenderShape.MODEL;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return state.getValue(ANIMATED) ? new StackBlockEntity(pos, state) : null;
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
        super.onRemove(state, world, pos, newState, moved);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, Item.TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltipFlag) {
        list.add(Component.translatable("tooltip.foundation.canbeplaced").withStyle(ChatFormatting.GRAY));
    }
}
