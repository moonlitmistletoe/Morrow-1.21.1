package net.satisfy.foundation.registry;

import com.mojang.serialization.MapCodec;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.satisfy.foundation.Foundation;
import org.jetbrains.annotations.NotNull;

/** All particle types the lib ships. {@code DYE_SPLASH}, {@code COLORED_DRIP}, {@code FEATHER} and the {@code COLORED_SOUP_*} bubbles take a color. */
public final class FoundationParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(Foundation.MOD_ID, Registries.PARTICLE_TYPE);

    public static final RegistrySupplier<SimpleParticleType> SOUP_BUBBLE = PARTICLE_TYPES.register("soup_bubble", () -> new SimpleParticleType(false) {});
    public static final RegistrySupplier<SimpleParticleType> SOUP_STEAM = PARTICLE_TYPES.register("soup_steam", () -> new SimpleParticleType(false) {});
    public static final RegistrySupplier<SimpleParticleType> SOUP_COOKING_BUBBLE = PARTICLE_TYPES.register("soup_cooking_bubble", () -> new SimpleParticleType(false) {});
    public static final RegistrySupplier<ParticleType<ColorParticleOption>> COLORED_SOUP_BUBBLE = PARTICLE_TYPES.register("colored_soup_bubble", FoundationParticles::colored);
    public static final RegistrySupplier<ParticleType<ColorParticleOption>> COLORED_SOUP_COOKING_BUBBLE = PARTICLE_TYPES.register("colored_soup_cooking_bubble", FoundationParticles::colored);
    public static final RegistrySupplier<ParticleType<ColorParticleOption>> COLORED_STEAM = PARTICLE_TYPES.register("colored_steam", FoundationParticles::colored);
    public static final RegistrySupplier<ParticleType<ColorParticleOption>> DYE_SPLASH = PARTICLE_TYPES.register("dye_splash", FoundationParticles::colored);
    public static final RegistrySupplier<ParticleType<ColorParticleOption>> FEATHER = PARTICLE_TYPES.register("feather", FoundationParticles::colored);
    public static final RegistrySupplier<ParticleType<ColorParticleOption>> COLORED_DRIP = PARTICLE_TYPES.register("colored_drip", FoundationParticles::colored);
    public static final RegistrySupplier<SimpleParticleType> WATER_DRIP = PARTICLE_TYPES.register("water_drip", () -> new SimpleParticleType(false) {});
    public static final RegistrySupplier<SimpleParticleType> WATER_SPLASH = PARTICLE_TYPES.register("water_splash", () -> new SimpleParticleType(false) {});
    public static final RegistrySupplier<SimpleParticleType> FIREFLY = PARTICLE_TYPES.register("firefly", () -> new SimpleParticleType(false) {});
    public static final RegistrySupplier<SimpleParticleType> LEAF = PARTICLE_TYPES.register("leaf", () -> new SimpleParticleType(false) {});

    private static ParticleType<ColorParticleOption> colored() {
        return new ParticleType<>(false) {
            @Override
            public @NotNull MapCodec<ColorParticleOption> codec() {
                return ColorParticleOption.codec(this);
            }

            @Override
            public @NotNull StreamCodec<? super RegistryFriendlyByteBuf, ColorParticleOption> streamCodec() {
                return ColorParticleOption.streamCodec(this);
            }
        };
    }

    private FoundationParticles() {
    }

    public static void init() {
        PARTICLE_TYPES.register();
    }
}
