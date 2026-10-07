package net.satisfy.foundation.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.*;
import java.util.*;

/** VoxelShape and placement helpers. */
public final class ShapeUtil {
    private ShapeUtil() {
    }

    /** Checks the block BELOW the pos. */
    public static boolean isFullAndSolid(LevelReader levelReader, BlockPos blockPos) {
        return isFaceFull(levelReader, blockPos) && isSolid(levelReader, blockPos);
    }

    public static boolean isFaceFull(LevelReader levelReader, BlockPos blockPos) {
        BlockPos belowPos = blockPos.below();
        return Block.isFaceFull(levelReader.getBlockState(belowPos).getBlockSupportShape(levelReader, belowPos), Direction.UP);
    }

    public static boolean isSolid(LevelReader levelReader, BlockPos blockPos) {
        return levelReader.getBlockState(blockPos.below()).isSolid();
    }

    /** Rotates a shape around y, clockwise from {@code from} to {@code to}. Build shapes facing north, then rotate. */
    public static VoxelShape rotateShape(Direction from, Direction to, VoxelShape shape) {
        VoxelShape[] buffer = new VoxelShape[]{shape, Shapes.empty()};
        int times = (to.get2DDataValue() - from.get2DDataValue() + 4) % 4;

        for(int i = 0; i < times; ++i) {
            buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> {
                buffer[1] = Shapes.joinUnoptimized(buffer[1], Shapes.box(1.0 - maxZ, minY, minX, 1.0 - minZ, maxY, maxX), BooleanOp.OR);
            });
            buffer[0] = buffer[1];
            buffer[1] = Shapes.empty();
        }

        return buffer[0];
    }

    /**
     * Hit position on the front face as (x, y), 0-1, seen from the front.
     * Empty if a forbidden or side face was hit.
     */
    public static Optional<Tuple<Float, Float>> getRelativeHitCoordinatesForBlockFace(BlockHitResult blockHitResult, Direction direction, Direction[] unAllowedDirections) {
        Direction direction2 = blockHitResult.getDirection();
        if (Arrays.stream(unAllowedDirections).toList().contains(direction2)) {
            return Optional.empty();
        } else if (direction != direction2 && direction2 != Direction.UP && direction2 != Direction.DOWN) {
            return Optional.empty();
        } else {
            BlockPos blockPos = blockHitResult.getBlockPos().relative(direction2);
            Vec3 vec3 = blockHitResult.getLocation().subtract((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ());
            float d = (float)vec3.x();
            float f = (float)vec3.z();
            float y = (float)vec3.y();
            if (direction2 == Direction.UP || direction2 == Direction.DOWN) {
                direction2 = direction;
            }

            Optional var10000;
            switch (direction2) {
                case NORTH:
                    var10000 = Optional.of(new Tuple((float)(1.0 - (double)d), y));
                    break;
                case SOUTH:
                    var10000 = Optional.of(new Tuple(d, y));
                    break;
                case WEST:
                    var10000 = Optional.of(new Tuple(f, y));
                    break;
                case EAST:
                    var10000 = Optional.of(new Tuple((float)(1.0 - (double)f), y));
                    break;
                case DOWN:
                case UP:
                    var10000 = Optional.empty();
                    break;
                default:
                    throw new IncompatibleClassChangeError();
            }

            return var10000;
        }
    }
}
