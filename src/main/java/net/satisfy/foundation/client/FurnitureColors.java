package net.satisfy.foundation.client;

import dev.architectury.registry.client.rendering.ColorHandlerRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.satisfy.foundation.block.SofaBlock;
import net.satisfy.foundation.block.TableBlock;

import java.util.ArrayList;
import java.util.List;

public final class FurnitureColors {
    private FurnitureColors() {
    }

    public static void init() {
        List<Block> tables = new ArrayList<>();
        List<Block> sofas = new ArrayList<>();
        for (Block block : BuiltInRegistries.BLOCK) {
            if (block instanceof TableBlock) tables.add(block);
            if (block instanceof SofaBlock) sofas.add(block);
        }
        if (!tables.isEmpty()) {
            ColorHandlerRegistry.registerBlockColors((state, level, pos, tintIndex) -> {
                return state.getValue(TableBlock.TABLECLOTH).tint();
            }, tables.toArray(new Block[0]));
        }
        if (!sofas.isEmpty()) {
            ColorHandlerRegistry.registerBlockColors((state, level, pos, tintIndex) -> state.getValue(SofaBlock.COLOR).tint(), sofas.toArray(new Block[0]));
        }
    }
}
