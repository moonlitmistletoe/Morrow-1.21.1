package net.satisfy.foundation.block;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.function.ToIntFunction;

public class LampBlock extends LanternBlock {
    public static final BooleanProperty LUMINANCE = BooleanProperty.create("luminance");

    private final VoxelShape standingShape;
    private final VoxelShape hangingShape;

    public LampBlock(Properties properties, VoxelShape standingShape, VoxelShape hangingShape) {
        super(properties);
        this.standingShape = standingShape;
        this.hangingShape = hangingShape;
        registerDefaultState(defaultBlockState().setValue(HANGING, false).setValue(WATERLOGGED, false).setValue(LUMINANCE, false));
    }

    public static ToIntFunction<BlockState> lightWhenOn(int level) {
        return state -> state.getValue(LUMINANCE) ? level : 0;
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(HANGING) ? hangingShape : standingShape;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(LUMINANCE);
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide()) {
            level.setBlockAndUpdate(pos, state.cycle(LUMINANCE));
            level.playSound(null, pos, SoundEvents.WOODEN_PRESSURE_PLATE_CLICK_ON, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }
}
