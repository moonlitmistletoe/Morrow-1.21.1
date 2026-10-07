package net.satisfy.foundation.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.util.Mth;
import net.satisfy.foundation.util.ShapeUtil;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;

/** Like {@link StackableBlock}, but sneak + empty hand eats one layer instead. */
@SuppressWarnings("all")
public class StackableEatableBlock extends Block implements EntityBlock {
    private static final IntegerProperty STACK_PROPERTY = StackableBlock.STACK_PROPERTY;
    private static final BooleanProperty ANIMATED = StackableBlock.ANIMATED;
    private static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    private final int maxStack;
    private static final VoxelShape SHAPE = Block.box(2, 0, 2, 14, 10, 14);
    private final VoxelShape[][] shapes;

    public StackableEatableBlock(Properties settings, int maxStack) {
        super(settings);
        this.maxStack = maxStack;
        this.shapes = null;
        this.registerDefaultState(this.stateDefinition.any().setValue(STACK_PROPERTY, 1).setValue(FACING, Direction.NORTH).setValue(ANIMATED, false));
    }

    public StackableEatableBlock(Properties settings, int maxStack, VoxelShape... shapesNorthByStack) {
        super(settings);
        this.maxStack = maxStack;
        this.shapes = new VoxelShape[shapesNorthByStack.length][4];
        for (int stack = 0; stack < shapesNorthByStack.length; stack++) {
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                this.shapes[stack][direction.get2DDataValue()] = ShapeUtil.rotateShape(Direction.NORTH, direction, shapesNorthByStack[stack]);
            }
        }
        this.registerDefaultState(this.stateDefinition.any().setValue(STACK_PROPERTY, 1).setValue(FACING, Direction.NORTH).setValue(ANIMATED, false));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        if (shapes == null || shapes.length == 0) {
            return SHAPE;
        }
        return shapes[Mth.clamp(state.getValue(STACK_PROPERTY) - 1, 0, shapes.length - 1)][state.getValue(FACING).get2DDataValue()];
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(STACK_PROPERTY, FACING, ANIMATED);
    }

    @Override
    public @NotNull BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public @NotNull BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()).setValue(ANIMATED, true);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        if (player.isShiftKeyDown() && stack.isEmpty()) {
            if (!world.isClientSide) {
                if (state.getValue(STACK_PROPERTY) > 1) {
                    world.setBlock(pos, state.setValue(STACK_PROPERTY, state.getValue(STACK_PROPERTY) - 1).setValue(ANIMATED, true), Block.UPDATE_ALL);
                } else {
                    world.removeBlock(pos, false);
                }
                player.getFoodData().eat(3, 0.6f);
                world.playSound(null, pos, SoundEvents.GENERIC_EAT, SoundSource.PLAYERS, 1.0F, 1.0F);
            }
            if (world.isClientSide) {
                for (int i = 0; i < 10; i++) {
                    double rx = world.random.nextDouble() - 0.5;
                    double ry = world.random.nextDouble();
                    double rz = world.random.nextDouble() - 0.5;
                    Vector3d velocity = new Vector3d(rx, ry, rz).mul(0.1);
                    world.addParticle(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(this.asItem())),
                            pos.getX() + 0.5, pos.getY() + 0.7, pos.getZ() + 0.5, velocity.x, velocity.y, velocity.z);
                }
            }
            return ItemInteractionResult.sidedSuccess(world.isClientSide);
        } else if (stack.getItem() == this.asItem()) {
            if (state.getValue(STACK_PROPERTY) < this.maxStack) {
                world.setBlock(pos, state.setValue(STACK_PROPERTY, state.getValue(STACK_PROPERTY) + 1).setValue(ANIMATED, true), Block.UPDATE_ALL);
                if (!player.isCreative()) {
                    stack.shrink(1);
                }
                for (int i = 0; i < 8; i++) {
                    double angle = world.random.nextDouble() * Math.PI * 2;
                    double speed = 0.1 + world.random.nextDouble() * 0.1;
                    double dx = Math.cos(angle) * speed;
                    double dy = 0.05;
                    double dz = Math.sin(angle) * speed;
                    world.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, state),
                            pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, dx, dy, dz);
                }
                return ItemInteractionResult.SUCCESS;
            }
        } else if (stack.isEmpty()) {
            if (state.getValue(STACK_PROPERTY) > 1) {
                world.setBlock(pos, state.setValue(STACK_PROPERTY, state.getValue(STACK_PROPERTY) - 1).setValue(ANIMATED, true), Block.UPDATE_ALL);
            } else if (state.getValue(STACK_PROPERTY) == 1) {
                world.destroyBlock(pos, false);
            }
            Direction direction = player.getDirection().getOpposite();
            Block.popResourceFromFace(world, pos, direction, new ItemStack(this));
            return ItemInteractionResult.SUCCESS;
        }
        return super.useItemOn(stack, state, world, pos, player, interactionHand, blockHitResult);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return state.getValue(ANIMATED) ? RenderShape.ENTITYBLOCK_ANIMATED : RenderShape.MODEL;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return state.getValue(ANIMATED) ? new StackBlockEntity(pos, state) : null;
    }
}
