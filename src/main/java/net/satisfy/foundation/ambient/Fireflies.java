package net.satisfy.foundation.ambient;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.satisfy.foundation.registry.FoundationParticles;

/**
 * Small helper around the firefly particle.
 * Nights only, needs some sky light and no rain.
 */
public final class Fireflies {
    private static final long DAY_LENGTH = 24000L;
    private static final long NIGHT_START = 12500L;
    private static final long NIGHT_END = 23500L;
    private static final int MIN_SKY_LIGHT = 10;

    private Fireflies() {
    }

    /** @return true if fireflies should be around at this pos right now */
    public static boolean isActive(Level level, BlockPos pos) {
        long time = level.getDayTime() % DAY_LENGTH;
        return time > NIGHT_START && time < NIGHT_END && level.getBrightness(LightLayer.SKY, pos) >= MIN_SKY_LIGHT && !level.isRaining();
    }

    /**
     * Spawns one firefly roughly around the block.
     *
     * @param spread horizontal spread in blocks
     * @param height y offset from the block bottom
     */
    public static void spawn(Level level, BlockPos pos, RandomSource random, double spread, double height) {
        level.addParticle(FoundationParticles.FIREFLY.get(),
                pos.getX() + 0.5 + (random.nextDouble() - 0.5) * spread * 2,
                pos.getY() + height + random.nextDouble() * 0.6,
                pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * spread * 2,
                0.0, 0.0, 0.0);
    }
}
