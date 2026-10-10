package net.moonlitmistletoe.morrow.block;

import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks("morrow");

    public static final DeferredBlock<BlackberryCropBlock> BLACKBERRY_CROP;
    public static final DeferredBlock<BlueberryCropBlock> BLUEBERRY_CROP;
    public static final DeferredBlock<CoffeeCropBlock> COFFEE_CROP;

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }

    static {
        BLACKBERRY_CROP = BLOCKS.register("blackberry_crop",
                () -> new BlackberryCropBlock(Blocks.WHEAT.properties()));

        BLUEBERRY_CROP = BLOCKS.register("blueberry_crop",
                () -> new BlueberryCropBlock(Blocks.WHEAT.properties()));

        COFFEE_CROP = BLOCKS.register("coffee_crop",
                () -> new CoffeeCropBlock(Blocks.WHEAT.properties()));

    }
}