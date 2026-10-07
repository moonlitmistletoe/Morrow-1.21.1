package net.satisfy.bakery;

import net.minecraft.resources.ResourceLocation;
import net.moonlitmistletoe.whatsits.Whatsits;
import net.satisfy.bakery.core.registry.ObjectRegistry;
import net.satisfy.bakery.core.registry.SoundEventRegistry;

public final class Bakery {
    public static final String MOD_ID = Whatsits.MOD_ID;

    public static ResourceLocation identifier(String name) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, name);
    }

    public static void init() {
        ObjectRegistry.init();
        SoundEventRegistry.init();
    }
}
