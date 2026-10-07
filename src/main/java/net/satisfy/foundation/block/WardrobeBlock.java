package net.satisfy.foundation.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class WardrobeBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public static final MapCodec<WardrobeBlock> CODEC = simpleCodec(properties -> new WardrobeBlock(properties, () -> null));
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    private static final VoxelShape LOWER_SHAPE = Shapes.or(
            Block.box(0.0D, 0.0D, 0.0D, 4.0D, 2.0D, 4.0D),
            Block.box(12.0D, 0.0D, 0.0D, 16.0D, 2.0D, 4.0D),
            Block.box(0.0D, 0.0D, 12.0D, 4.0D, 2.0D, 16.0D),
            Block.box(12.0D, 0.0D, 12.0D, 16.0D, 2.0D, 16.0D),
            Block.box(0.0D, 2.0D, 0.0D, 16.0D, 16.0D, 16.0D));

    private final Supplier<? extends BlockEntityType<? extends WardrobeBlockEntity>> blockEntityType;

    public WardrobeBlock(Properties properties, Supplier<? extends BlockEntityType<? extends WardrobeBlockEntity>> blockEntityType) {
        super(properties);
        this.blockEntityType = blockEntityType;
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(OPEN, false).setValue(HALF, DoubleBlockHalf.LOWER));
    }

    @Override
    protected @NotNull MapCodec<? extends WardrobeBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, OPEN, HALF);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();
        if (pos.getY() < level.getMaxBuildHeight() - 1 && level.getBlockState(pos.above()).canBeReplaced(context)) {
            return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
        }
        return null;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        level.setBlock(pos.above(), state.setValue(HALF, DoubleBlockHalf.UPPER), 3);
    }

    @Override
    public @NotNull BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide) {
            DoubleBlockHalf half = state.getValue(HALF);
            BlockPos basePos = half == DoubleBlockHalf.LOWER ? pos : pos.below();
            if (level.getBlockEntity(basePos) instanceof WardrobeBlockEntity wardrobe) {
                for (int slot = 0; slot < wardrobe.getInventory().size(); slot++) {
                    ItemStack stored = wardrobe.getItem(slot);
                    if (!stored.isEmpty()) {
                        Block.popResource(level, basePos, stored.copy());
                        wardrobe.setStack(slot, ItemStack.EMPTY);
                    }
                }
            }

            BlockPos otherPos = half == DoubleBlockHalf.LOWER ? pos.above() : pos.below();
            BlockState other = level.getBlockState(otherPos);
            if (other.is(this) && other.getValue(HALF) != half) {
                level.setBlock(otherPos, Blocks.AIR.defaultBlockState(), 35);
                level.levelEvent(player, 2001, otherPos, Block.getId(other));
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        BlockPos basePos = basePos(state, pos);
        if (!(level.getBlockEntity(basePos) instanceof WardrobeBlockEntity wardrobe)) {
            return InteractionResult.PASS;
        }
        if (player.isShiftKeyDown()) {
            for (int slot = 0; slot < wardrobe.getInventory().size(); slot++) {
                ItemStack stored = wardrobe.getItem(slot);
                if (!stored.isEmpty()) {
                    player.addItem(stored.copy());
                    wardrobe.setStack(slot, ItemStack.EMPTY);
                    return InteractionResult.CONSUME;
                }
            }
            return InteractionResult.PASS;
        }
        toggleOpen(level, basePos);
        return InteractionResult.CONSUME;
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!(stack.getItem() instanceof ArmorItem armor)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        int slot = WardrobeBlockEntity.slotFor(armor.getEquipmentSlot());
        if (slot < 0) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (level.isClientSide) {
            return ItemInteractionResult.SUCCESS;
        }
        if (!(level.getBlockEntity(basePos(state, pos)) instanceof WardrobeBlockEntity wardrobe)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        ItemStack existing = wardrobe.getItem(slot);
        wardrobe.setStack(slot, stack.copyWithCount(1));
        stack.shrink(1);
        if (!existing.isEmpty()) {
            player.addItem(existing);
        }
        return ItemInteractionResult.CONSUME;
    }

    private static BlockPos basePos(BlockState state, BlockPos pos) {
        return state.getValue(HALF) == DoubleBlockHalf.UPPER ? pos.below() : pos;
    }

    private void toggleOpen(Level level, BlockPos basePos) {
        BlockState state = level.getBlockState(basePos);
        if (!state.is(this)) {
            return;
        }
        boolean open = !state.getValue(OPEN);
        level.setBlock(basePos, state.setValue(OPEN, open), 3);
        BlockState upper = level.getBlockState(basePos.above());
        if (upper.is(this)) {
            level.setBlock(basePos.above(), upper.setValue(OPEN, open), 3);
        }
        level.playSound(null, basePos, open ? SoundEvents.WOODEN_DOOR_OPEN : SoundEvents.WOODEN_DOOR_CLOSE, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    @Override
    protected @NotNull BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected @NotNull BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected boolean useShapeForLightOcclusion(BlockState state) {
        return true;
    }

    @Override
    protected @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(HALF) == DoubleBlockHalf.UPPER ? Shapes.block() : LOWER_SHAPE;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return state.getValue(HALF) == DoubleBlockHalf.LOWER ? blockEntityType.get().create(pos, state) : null;
    }
}
