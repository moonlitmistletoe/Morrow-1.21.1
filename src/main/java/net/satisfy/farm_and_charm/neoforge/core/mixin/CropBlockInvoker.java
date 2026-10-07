package net.satisfy.farm_and_charm.neoforge.core.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(CropBlock.class)
public interface CropBlockInvoker {
    @Invoker("randomTick")
    void morrow$randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random);
}
