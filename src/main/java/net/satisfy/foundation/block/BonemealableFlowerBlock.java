package net.satisfy.foundation.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

/** Flower that drops a copy of itself when bonemealed (60% chance). Like vanilla tall flowers. */
public class BonemealableFlowerBlock extends BushBlock implements BonemealableBlock {

    public static final MapCodec<BonemealableFlowerBlock> CODEC =
            simpleCodec(BonemealableFlowerBlock::new);

    private static final float RETURN_CHANCE = 0.6F;

    public BonemealableFlowerBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected @NotNull MapCodec<? extends BushBlock> codec() {
        return CODEC;
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader levelReader, BlockPos blockPos, BlockState blockState) {
        return true;
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource randomSource, BlockPos blockPos, BlockState blockState) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel serverLevel, RandomSource randomSource, BlockPos blockPos, BlockState blockState) {
        if (randomSource.nextFloat() < RETURN_CHANCE) {
            popResource(serverLevel, blockPos, new ItemStack(this));
        }
    }
}