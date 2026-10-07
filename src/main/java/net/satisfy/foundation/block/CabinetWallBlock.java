package net.satisfy.foundation.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.foundation.util.ShapeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

public class CabinetWallBlock extends CabinetBlock {
    private static final Map<Direction, VoxelShape> SHAPES = new EnumMap<>(Direction.class);

    static {
        VoxelShape north = Shapes.box(0, 0, 0.25, 1, 1, 1);
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            SHAPES.put(direction, ShapeUtil.rotateShape(Direction.NORTH, direction, north));
        }
    }

    public CabinetWallBlock(Properties properties, Supplier<? extends BlockEntityType<? extends CabinetBlockEntity>> blockEntityType) {
        super(properties, blockEntityType);
    }

    public CabinetWallBlock(Properties properties, Supplier<? extends BlockEntityType<? extends CabinetBlockEntity>> blockEntityType, @Nullable SoundEvent openSound, @Nullable SoundEvent closeSound) {
        super(properties, blockEntityType, openSound, closeSound);
    }

    @Override
    protected @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES.get(state.getValue(FACING));
    }
}
