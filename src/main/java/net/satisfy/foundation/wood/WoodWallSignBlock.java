package net.satisfy.foundation.wood;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class WoodWallSignBlock extends WallSignBlock {
    private final Supplier<? extends BlockEntityType<?>> blockEntityType;

    public WoodWallSignBlock(WoodType woodType, Properties properties, Supplier<? extends BlockEntityType<?>> blockEntityType) {
        super(woodType, properties);
        this.blockEntityType = blockEntityType;
    }

    @Override
    public @NotNull BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WoodSignBlockEntity(blockEntityType.get(), pos, state);
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return type == blockEntityType.get() ? (tickLevel, pos, tickState, blockEntity) -> SignBlockEntity.tick(tickLevel, pos, tickState, (SignBlockEntity) blockEntity) : null;
    }
}
