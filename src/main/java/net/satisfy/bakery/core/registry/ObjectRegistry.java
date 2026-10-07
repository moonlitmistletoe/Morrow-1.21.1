package net.satisfy.bakery.core.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.satisfy.bakery.Bakery;
import net.satisfy.bakery.core.block.*;
import java.util.function.Supplier;

public final class ObjectRegistry {
    private static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(Bakery.MOD_ID, Registries.BLOCK);
    private static final Registrar<Block> BLOCK_REGISTRAR = BLOCKS.getRegistrar();


    public static void init() {
        BLOCKS.register();
    }

    public static <T extends Block> RegistrySupplier<T> registerWithoutItem(String name, Supplier<T> block, Object... ignored) {
        return BLOCKS.register(Bakery.identifier(name), block);
    }
}
