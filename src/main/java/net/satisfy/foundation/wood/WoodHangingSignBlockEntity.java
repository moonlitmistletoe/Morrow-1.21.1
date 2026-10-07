package net.satisfy.foundation.wood;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.HangingSignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class WoodHangingSignBlockEntity extends HangingSignBlockEntity {
    private final BlockEntityType<?> type;

    public WoodHangingSignBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(pos, state);
        this.type = type;
    }

    @Override
    public @NotNull BlockEntityType<?> getType() {
        return type;
    }

    @Override
    public boolean isValidBlockState(BlockState state) {
        return state.getBlock() instanceof SignBlock;
    }
}
