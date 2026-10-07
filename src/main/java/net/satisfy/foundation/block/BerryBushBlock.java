package net.satisfy.foundation.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.function.Supplier;

/**
 * Generic berry bush, basically the vanilla sweet berry bush without the damage.
 * Optional blossom item can be picked at {@link #BLOOMING_AGE}.
 * Override {@link #growthChance()} or {@link #bonusYield(Level, BlockPos)} if you need something else.
 */
public class BerryBushBlock extends BushBlock implements BonemealableBlock {
    public static final int BLOOMING_AGE = 2;
    public static final int MAX_AGE = 3;
    public static final IntegerProperty AGE = BlockStateProperties.AGE_3;
    private static final VoxelShape SAPLING_SHAPE = Block.box(3.0, 0.0, 3.0, 13.0, 8.0, 13.0);
    private static final VoxelShape GROWN_SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 16.0, 15.0);

    private final Supplier<? extends ItemLike> berry;
    private final Supplier<? extends ItemLike> blossom;

    /** Bush without blossoms. */
    public BerryBushBlock(Properties properties, Supplier<? extends ItemLike> berry) {
        this(properties, berry, null);
    }

    /** @param blossom item you get when harvesting at blooming age, can be null */
    public BerryBushBlock(Properties properties, Supplier<? extends ItemLike> berry, Supplier<? extends ItemLike> blossom) {
        super(properties);
        this.berry = berry;
        this.blossom = blossom;
        this.registerDefaultState(this.stateDefinition.any().setValue(AGE, 0));
    }

    @Override
    protected MapCodec<? extends BushBlock> codec() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return new ItemStack(berry.get());
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(AGE) == 0 ? SAPLING_SHAPE : GROWN_SHAPE;
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return state.getValue(AGE) < MAX_AGE;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (level.getRawBrightness(pos.above(), 0) >= 9 && random.nextInt(growthChance()) == 0) {
            grow(state, level, pos);
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        int age = state.getValue(AGE);
        boolean pickBlossom = blossom != null && age == BLOOMING_AGE;
        if (age < MAX_AGE && !pickBlossom) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide) {
            int count = 1 + level.random.nextInt(2) + bonusYield(level, pos);
            popResource(level, pos, new ItemStack(pickBlossom ? blossom.get() : berry.get(), count));
            level.playSound(null, pos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS, 1.0F, 0.8F + level.random.nextFloat() * 0.4F);
            BlockState harvested = state.setValue(AGE, 1);
            level.setBlock(pos, harvested, Block.UPDATE_CLIENTS);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, harvested));
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    /** 1 in X chance per random tick to grow. Default 5. */
    protected int growthChance() {
        return 5;
    }

    /** Extra berries on harvest, 0 by default. Nice for fertilizer stuff. */
    protected int bonusYield(Level level, BlockPos pos) {
        return 0;
    }

    private void grow(BlockState state, ServerLevel level, BlockPos pos) {
        BlockState grown = state.setValue(AGE, Math.min(MAX_AGE, state.getValue(AGE) + 1));
        level.setBlock(pos, grown, Block.UPDATE_CLIENTS);
        level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(grown));
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        return state.getValue(AGE) < MAX_AGE;
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        grow(state, level, pos);
    }
}
