package net.satisfy.foundation.ambient;

import net.satisfy.foundation.block.LampBlock;
import dev.architectury.event.events.client.ClientTickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import net.satisfy.foundation.ambient.Fireflies;
import net.satisfy.foundation.registry.FoundationTags;

/**
 * Client side ambience that spawns fireflies around blocks tagged with
 * {@code foundation:attracts_fireflies}. Lit property is respected (unlit lanterns = no bugs).
 */
public final class FireflyAmbience {
    private static final List<BooleanSupplier> CONDITIONS = new ArrayList<>();
    private static boolean initialized;
    private static final int RANGE = 12;
    private static final int SAMPLES = 390;
    private static final int CHANCE = 2;
    private static final double SPREAD = 1.5;
    private static final double HANGING_OFFSET = 0.1;
    private static final double STANDING_OFFSET = 0.2;

    private FireflyAmbience() {
    }

    /**
     * Hooks the ambience into the client tick. Can be called by several mods,
     * every condition gets and-ed together so one mod can turn it off (e.g. via config).
     *
     * @param enabled condition for this caller, usually a config value
     */
    public static void init(BooleanSupplier enabled) {
        CONDITIONS.add(enabled);
        if (!initialized) {
            initialized = true;
            ClientTickEvent.CLIENT_POST.register(FireflyAmbience::tick);
        }
    }

    private static boolean isEnabled() {
        return CONDITIONS.stream().allMatch(BooleanSupplier::getAsBoolean);
    }

    private static void tick(Minecraft minecraft) {
        Level level = minecraft.level;
        if (level == null || minecraft.player == null || minecraft.isPaused() || !isEnabled()) {
            return;
        }
        RandomSource random = level.random;
        BlockPos center = minecraft.player.blockPosition();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int i = 0; i < SAMPLES; i++) {
            pos.set(center.getX() + random.nextInt(RANGE * 2 + 1) - RANGE, center.getY() + random.nextInt(RANGE * 2 + 1) - RANGE, center.getZ() + random.nextInt(RANGE * 2 + 1) - RANGE);
            BlockState state = level.getBlockState(pos);
            if (!state.is(FoundationTags.ATTRACTS_FIREFLIES) || random.nextInt(CHANCE) != 0) {
                continue;
            }
            if (state.hasProperty(BlockStateProperties.LIT) && !state.getValue(BlockStateProperties.LIT) || state.hasProperty(LampBlock.LUMINANCE) && !state.getValue(LampBlock.LUMINANCE)) {
                continue;
            }
            if (!Fireflies.isActive(level, pos)) {
                continue;
            }
            boolean hanging = state.hasProperty(BlockStateProperties.HANGING) && state.getValue(BlockStateProperties.HANGING);
            Fireflies.spawn(level, pos, random, SPREAD, hanging ? HANGING_OFFSET : STANDING_OFFSET);
        }
    }
}
