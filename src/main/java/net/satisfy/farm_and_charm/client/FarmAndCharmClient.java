package net.satisfy.farm_and_charm.client;

import dev.architectury.registry.client.level.entity.EntityModelLayerRegistry;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import dev.architectury.registry.client.particle.ParticleProviderRegistry;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import dev.architectury.registry.client.rendering.ColorHandlerRegistry;
import dev.architectury.registry.client.rendering.RenderTypeRegistry;
import dev.architectury.registry.menu.MenuRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.satisfy.farm_and_charm.client.event.ClientEventHandler;
import net.satisfy.farm_and_charm.client.gui.CookingPotGui;
import net.satisfy.farm_and_charm.client.gui.PetBowlEditGui;
import net.satisfy.farm_and_charm.client.gui.StoveGui;
import net.satisfy.farm_and_charm.client.model.*;
import net.satisfy.farm_and_charm.client.particle.SoupBubbleParticle;
import net.satisfy.farm_and_charm.client.particle.SoupCookingBubbleParticle;
import net.satisfy.farm_and_charm.client.particle.SoupSteamParticle;
import net.satisfy.farm_and_charm.client.renderer.block.*;
import net.satisfy.farm_and_charm.client.renderer.entity.PlowCartRenderer;
import net.satisfy.farm_and_charm.client.renderer.entity.SupplyCartRenderer;
import net.satisfy.farm_and_charm.core.block.entity.PetBowlBlockEntity;
import net.satisfy.farm_and_charm.core.registry.EntityTypeRegistry;
import net.satisfy.farm_and_charm.core.registry.ParticleTypeRegistry;
import net.satisfy.farm_and_charm.core.registry.ScreenhandlerTypeRegistry;
import net.satisfy.farm_and_charm.core.registry.StorageTypeRegistry;

import static net.satisfy.farm_and_charm.core.registry.ObjectRegistry.*;

public class FarmAndCharmClient {

    public static void onInitializeClient() {
        RenderTypeRegistry.register(RenderType.cutout(), CRAFTING_BOWL.get(), WATER_SPRINKLER.get(),
                STOVE.get(), WILD_CARROTS.get(), NETTLE_TEA.get(), STRAWBERRY_TEA.get(), WILD_BEETROOTS.get(), WILD_NETTLE.get(),
                WILD_POTATOES.get(), CHICKEN_NEST.get(), STURDY_LADDER.get(),  FEATHER_PILE.get(), WHEAT_PILE.get()
        );

        ParticleProviderRegistry.register(ParticleTypeRegistry.SOUP_BUBBLE.get(), SoupBubbleParticle.Provider::new);
        ParticleProviderRegistry.register(ParticleTypeRegistry.SOUP_STEAM.get(), SoupSteamParticle.Provider::new);
        ParticleProviderRegistry.register(ParticleTypeRegistry.SOUP_COOKING_BUBBLE.get(), SoupCookingBubbleParticle.Provider::new);

        ColorHandlerRegistry.registerBlockColors((state, world, pos, tintIndex) -> {
            if (world == null || pos == null) {
                return -1;
            }
            return BiomeColors.getAverageWaterColor(world, pos);
        }, WATER_TROUGH.get()
        );

        ClientStorageTypes.init();
        ClientEventHandler.init();
        registerStorageTypeRenderers();
        registerBlockEntityRenderer();
        MenuRegistry.registerScreenFactory(ScreenhandlerTypeRegistry.COOKING_POT_SCREEN_HANDLER.get(), CookingPotGui::new);
        MenuRegistry.registerScreenFactory(ScreenhandlerTypeRegistry.STOVE_SCREEN_HANDLER.get(), StoveGui::new);
        MenuRegistry.registerScreenFactory(ScreenhandlerTypeRegistry.ROASTER_SCREEN_HANDLER.get(), RoasterGui::new);
    }

    public static void registerEntityRenderers() {
        EntityRendererRegistry.register(EntityTypeRegistry.ROTTEN_TOMATO, ThrownItemRenderer::new);
        EntityRendererRegistry.register(EntityTypeRegistry.SUPPLY_CART, SupplyCartRenderer::new);
        EntityRendererRegistry.register(EntityTypeRegistry.PLOW, PlowCartRenderer::new);
        EntityRendererRegistry.register(EntityTypeRegistry.CHAIR, ChairRenderer::new);
    }


    public static void preInitClient() {
        registerEntityRenderers();
        registerEntityModelLayer();
    }

    public static void registerEntityModelLayer() {
        EntityModelLayerRegistry.register(WaterSprinklerModel.LAYER_LOCATION, WaterSprinklerModel::getTexturedModelData);
        EntityModelLayerRegistry.register(CraftingBowlModel.LAYER_LOCATION, CraftingBowlModel::getTexturedModelData);
        EntityModelLayerRegistry.register(MincerModel.LAYER_LOCATION, MincerModel::getTexturedModelData);
        EntityModelLayerRegistry.register(ScarecrowModel.LAYER_LOCATION, ScarecrowModel::getTexturedModelData);
        EntityModelLayerRegistry.register(SupplyCartModel.LAYER_LOCATION, SupplyCartModel::createBodyLayer);
        EntityModelLayerRegistry.register(PlowCartModel.LAYER_LOCATION, PlowCartModel::createBodyLayer);
        EntityModelLayerRegistry.register(DungareesLeggingsModel.LAYER_LOCATION, DungareesLeggingsModel::createBodyLayer);
    }

    public static void registerStorageTypeRenderers() {
        StorageBlockEntityRenderer.registerStorageType(StorageTypeRegistry.TOOL_RACK, new ToolRackRenderer());
        StorageBlockEntityRenderer.registerStorageType(StorageTypeRegistry.WINDOW_SILL, new WindowSillRenderer());
        StorageBlockEntityRenderer.registerStorageType(StorageTypeRegistry.CHICKEN_NEST, new ChickenNestRenderer());
    }

    public static void registerBlockEntityRenderer() {
        BlockEntityRendererRegistry.register(EntityTypeRegistry.ROPE_KNOT_BLOCK_ENTITY.get(), ctx -> new RopeKnotRenderer());
        BlockEntityRendererRegistry.register(EntityTypeRegistry.STOVE_BLOCK_ENTITY.get(), StoveBlockRenderer::new);
        BlockEntityRendererRegistry.register(EntityTypeRegistry.SCARECROW_BLOCK_ENTITY.get(), ScarecrowRenderer::new);
        BlockEntityRendererRegistry.register(EntityTypeRegistry.MINCER_BLOCK_ENTITY.get(), MincerRenderer::new);
        BlockEntityRendererRegistry.register(EntityTypeRegistry.CRAFTING_BOWL_BLOCK_ENTITY.get(), CraftingBowlRenderer::new);
        BlockEntityRendererRegistry.register(EntityTypeRegistry.SPRINKLER_BLOCK_ENTITY.get(), WaterSprinklerRenderer::new);
        BlockEntityRendererRegistry.register(EntityTypeRegistry.PET_BOWL_BLOCK_ENTITY.get(), context -> new PetBowlBlockRenderer());
        BlockEntityRendererRegistry.register(EntityTypeRegistry.STORAGE_ENTITY.get(), context -> new StorageBlockEntityRenderer());
    }

    public static void openPetBowlScreen(PetBowlBlockEntity entity) {
        Minecraft.getInstance().setScreen(new PetBowlEditGui(entity));
    }

}