package net.satisfy.foundation.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.satisfy.foundation.Foundation;

/** Tags used by the lib, fill them via datapack from your mod. */
public final class FoundationTags {
    public static final TagKey<Item> CAKE_CUTTERS = TagKey.create(Registries.ITEM, Foundation.identifier("cake_cutters"));
    public static final TagKey<Block> ATTRACTS_FIREFLIES = TagKey.create(Registries.BLOCK, Foundation.identifier("attracts_fireflies"));

    private FoundationTags() {
    }
}
