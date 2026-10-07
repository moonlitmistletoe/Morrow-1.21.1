package net.satisfy.foundation.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.FastColor;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.satisfy.foundation.registry.FoundationParticles;

/** Dyeing blocks with a nice splash effect. */
public final class DyeHelper {
    private static final int SPLASH_PARTICLES = 16;

    private DyeHelper() {
    }

    /** Sets the color property, passes if the block already has that color. */
    public static ItemInteractionResult dye(ItemStack stack, DyeColor color, BlockState state, EnumProperty<DyeColor> property, Level level, BlockPos pos, Player player) {
        if (state.getValue(property) == color) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        return apply(stack, color, state.setValue(property, color), level, pos, player);
    }

    /** Places the dyed state, uses one dye and spawns splash particles. */
    public static ItemInteractionResult apply(ItemStack stack, DyeColor color, BlockState dyed, Level level, BlockPos pos, Player player) {
        if (level instanceof ServerLevel serverLevel) {
            level.setBlock(pos, dyed, Block.UPDATE_ALL);
            stack.consume(1, player);
            level.playSound(null, pos, SoundEvents.DYE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
            ColorParticleOption splash = ColorParticleOption.create(FoundationParticles.DYE_SPLASH.get(), FastColor.ARGB32.opaque(color.getTextureDiffuseColor()));
            serverLevel.sendParticles(splash, pos.getX() + 0.5, pos.getY() + 0.4, pos.getZ() + 0.5, SPLASH_PARTICLES, 0.3, 0.1, 0.3, 0.08);
        }
        return ItemInteractionResult.sidedSuccess(level.isClientSide);
    }
}
