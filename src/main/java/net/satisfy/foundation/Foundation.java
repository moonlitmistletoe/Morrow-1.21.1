package net.satisfy.foundation;

import net.satisfy.foundation.overlay.BlockNotice;
import net.minecraft.resources.ResourceLocation;
import net.satisfy.foundation.overlay.BlockInfoSync;
import net.satisfy.foundation.registry.FoundationBlockEntities;
import net.satisfy.foundation.registry.FoundationEntities;
import net.satisfy.foundation.registry.FoundationParticles;
import net.satisfy.foundation.recipe.RecipeUnlockSync;
import net.satisfy.foundation.recipe.book.PlaceRecipePacket;
import net.satisfy.foundation.text.SetTextPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Entry point of the Foundation lib.
 * Holds the mod id and kicks off all the common registries + packets.
 */
public class Foundation {
    public static final String MOD_ID = "foundation";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    /** Call this once from the loader specific init. Registers particles, entities and the network stuff. */
    public static void init() {
        FoundationParticles.init();
        FoundationEntities.init();
        FoundationBlockEntities.init();
        BlockInfoSync.init();
        BlockNotice.init();
        SetTextPacket.init();
        RecipeUnlockSync.init();
        PlaceRecipePacket.init();
    }

    /** Shortcut for {@code foundation:<path>}, saves some typing. */
    public static ResourceLocation identifier(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
