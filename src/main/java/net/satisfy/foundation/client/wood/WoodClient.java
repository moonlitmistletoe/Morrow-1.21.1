package net.satisfy.foundation.client.wood;

import dev.architectury.registry.client.level.entity.EntityModelLayerRegistry;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.ChestBoatModel;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.satisfy.foundation.wood.BoatWood;

public final class WoodClient {
    private WoodClient() {
    }

    public static void registerBoatLayers(BoatWood... woods) {
        for (BoatWood wood : woods) {
            EntityModelLayerRegistry.register(WoodBoatRenderer.layer(wood, false), BoatModel::createBodyModel);
            EntityModelLayerRegistry.register(WoodBoatRenderer.layer(wood, true), ChestBoatModel::createBodyModel);
        }
    }

    public static void registerSignMaterials(WoodType... woodTypes) {
        for (WoodType woodType : woodTypes) {
            ResourceLocation id = ResourceLocation.parse(woodType.name());
            Sheets.SIGN_MATERIALS.putIfAbsent(woodType, new Material(Sheets.SIGN_SHEET, id.withPrefix("entity/signs/")));
            Sheets.HANGING_SIGN_MATERIALS.putIfAbsent(woodType, new Material(Sheets.SIGN_SHEET, id.withPrefix("entity/signs/hanging/")));
        }
    }

    @SuppressWarnings("unchecked")
    public static void registerSignRenderers(BlockEntityType<?> sign, BlockEntityType<?> hangingSign) {
        BlockEntityRendererRegistry.register((BlockEntityType<SignBlockEntity>) sign, SignRenderer::new);
        BlockEntityRendererRegistry.register((BlockEntityType<SignBlockEntity>) hangingSign, HangingSignRenderer::new);
    }
}
