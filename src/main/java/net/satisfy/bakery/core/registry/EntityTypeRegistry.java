package net.satisfy.bakery.core.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.satisfy.bakery.Bakery;
import net.satisfy.bakery.core.block.entity.*;
import net.satisfy.farm_and_charm.FarmAndCharm;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class EntityTypeRegistry {
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Bakery.MOD_ID, Registries.BLOCK_ENTITY_TYPE);

    public static final RegistrySupplier<BlockEntityType<SmallCookingPotBlockEntity>> SMALL_COOKING_POT_BLOCK_ENTITY = registerBlockEntity("small_cooking_pot", () -> BlockEntityType.Builder.of(SmallCookingPotBlockEntity::new, ObjectRegistry.SMALL_COOKING_POT.get()).build(null));
    public static final RegistrySupplier<BlockEntityType<StorageBlockEntity>> STORAGE_ENTITY = registerBlockEntity("bakery_storage", () -> BlockEntityType.Builder.of(StorageBlockEntity::new, StorageTypeRegistry.registerBlocks(new HashSet<>()).toArray(new Block[0])).build(null));


    private static <T extends BlockEntityType<?>> RegistrySupplier<T> registerBlockEntity(String name, final Supplier<T> type) {
        return BLOCK_ENTITY_TYPES.register(Bakery.identifier(name), type);
    }

    public static void init() {
        BLOCK_ENTITY_TYPES.register();
    }
}
