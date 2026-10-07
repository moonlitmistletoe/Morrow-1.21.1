package net.satisfy.foundation.block;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Block you can stack up to {@code maxStack} times by clicking with the same item.
 * Empty hand takes one back.
 */
@SuppressWarnings("all")
public class StackableBlock extends Block implements EntityBlock {
    public static final IntegerProperty STACK_PROPERTY = IntegerProperty.create("stack", 1, 8);
    public static final BooleanProperty ANIMATED = BooleanProperty.create("animated");
    private static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    private final int maxStack;
    private static final VoxelShape SHAPE = Block.box(2, 0, 2, 14, 10, 14);

    public StackableBlock(Properties settings, int maxStack) {
        super(settings);
        this.maxStack = maxStack;
        this.registerDefaultState(this.stateDefinition.any().setValue(STACK_PROPERTY, 1).setValue(FACING, Direction.NORTH).setValue(ANIMATED, false));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
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
                    world.setBlock(pos, state.setValue(STACK_PROPERTY, state.getValue(STACK_PROPERTY) - 1).setValue(ANIMATED, true), 3);
                } else {
                    world.removeBlock(pos, false);
                }
                ItemStack droppedStack = new ItemStack(this.asItem());
                if (!player.getInventory().add(droppedStack)) {
                    Block.popResource(world, pos, droppedStack);
                }
                world.playSound(null, pos, SoundEvents.GENERIC_EAT, SoundSource.PLAYERS, 1.0F, 1.0F);
                if (world instanceof ServerLevel) {
                    ServerLevel serverWorld = (ServerLevel) world;
                    for (int count = 0; count < 10; ++count) {
                        double d0 = world.random.nextGaussian() * 0.02;
                        double d1 = world.random.nextGaussian() * 0.0;
                        double d2 = world.random.nextGaussian() * 0.02;
                        serverWorld.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, state), pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, 1, d0, d1, d2, 0.1);
                    }
                }
                return ItemInteractionResult.sidedSuccess(world.isClientSide);
            }
        } else if (stack.getItem() == this.asItem()) {
            if (state.getValue(STACK_PROPERTY) < this.maxStack) {
                world.setBlock(pos, state.setValue(STACK_PROPERTY, state.getValue(STACK_PROPERTY) + 1).setValue(ANIMATED, true), 3);
                if (!player.isCreative()) {
                    stack.shrink(1);
                }
                if (!world.isClientSide) {
                    SoundType sound = state.getSoundType();
                    world.playSound(null, pos, sound.getPlaceSound(), SoundSource.BLOCKS, (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * (0.9F + world.random.nextFloat() * 0.2F));
                }
                return ItemInteractionResult.SUCCESS;
            }
        } else if (stack.isEmpty()) {
            if (!world.isClientSide) {
                SoundType sound = state.getSoundType();
                world.playSound(null, pos, sound.getHitSound(), SoundSource.BLOCKS, (sound.getVolume() + 1.0F) / 2.0F, sound.getPitch() * 1.1F);
                world.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.3F, 1.0F + world.random.nextFloat() * 0.2F);
            }
            if (state.getValue(STACK_PROPERTY) > 1) {
                world.setBlock(pos, state.setValue(STACK_PROPERTY, state.getValue(STACK_PROPERTY) - 1).setValue(ANIMATED, true), 3);
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
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        VoxelShape shape = world.getBlockState(pos.below()).getBlockSupportShape(world, pos.below());
        Direction direction = Direction.UP;
        return Block.isFaceFull(shape, direction);
    }

    @Override
    public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        if (!state.canSurvive(world, pos)) {
            world.destroyBlock(pos, true);
        }
    }

    @Override
    public @NotNull BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        if (!state.canSurvive(world, pos)) {
            world.scheduleTick(pos, this, 1);
        }
        return super.updateShape(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, Item.TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltipFlag) {
        list.add(Component.translatable("tooltip.foundation.canbeplaced").withStyle(ChatFormatting.GRAY));
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
