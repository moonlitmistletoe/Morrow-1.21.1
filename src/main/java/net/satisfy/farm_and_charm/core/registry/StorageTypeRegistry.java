package net.satisfy.farm_and_charm.core.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.satisfy.farm_and_charm.FarmAndCharm;

import java.util.Set;

public class StorageTypeRegistry {
    public static final ResourceLocation CHICKEN_NEST = FarmAndCharm.identifier("chicken_nest");

    public static Set<Block> registerBlocks(Set<Block> blocks) {
        blocks.add(ObjectRegistry.CHICKEN_NEST.get());
        return blocks;
    }
}
