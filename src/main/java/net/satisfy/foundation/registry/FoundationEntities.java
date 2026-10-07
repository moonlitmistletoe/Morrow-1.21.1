package net.satisfy.foundation.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.satisfy.foundation.Foundation;
import net.satisfy.foundation.seat.ChairEntity;

/** Entity types of the lib. Only the invisible chair seat for now. */
public final class FoundationEntities {
    private static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Foundation.MOD_ID, Registries.ENTITY_TYPE);

    public static final RegistrySupplier<EntityType<ChairEntity>> CHAIR = ENTITY_TYPES.register("chair", () -> EntityType.Builder.of(ChairEntity::new, MobCategory.MISC).sized(0.001F, 0.001F).build(Foundation.identifier("chair").toString()));

    private FoundationEntities() {
    }

    public static void init() {
        ENTITY_TYPES.register();
    }
}
