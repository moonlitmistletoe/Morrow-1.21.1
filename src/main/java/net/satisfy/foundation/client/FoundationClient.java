package net.satisfy.foundation.client;

import net.satisfy.foundation.client.armor.ArmorSetTooltips;
import dev.architectury.registry.client.level.entity.EntityModelLayerRegistry;
import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import net.satisfy.foundation.client.render.SinkRenderer;
import net.satisfy.foundation.client.render.StackRenderer;
import net.satisfy.foundation.registry.FoundationBlockEntities;
import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import net.satisfy.foundation.banner.CompletionistBannerRenderer;
import net.satisfy.foundation.overlay.BlockInfoOverlay;
import net.satisfy.foundation.registry.FoundationEntities;
import net.satisfy.foundation.seat.ChairRenderer;
import net.satisfy.foundation.rarity.FoundationRarities;
import net.satisfy.foundation.tooltip.InfoTooltip;

/** Client side setup of the lib, called from the loader specific client entrypoints. */
public class FoundationClient {
    /** Model layers and entity renderers, needs to happen early. */
    public static void preInitClient() {
        EntityModelLayerRegistry.register(CompletionistBannerRenderer.LAYER_LOCATION, CompletionistBannerRenderer::createBodyLayer);
        EntityRendererRegistry.register(FoundationEntities.CHAIR, ChairRenderer::new);
    }

    public static void registerBlockEntityRenderers() {
        BlockEntityRendererRegistry.register(FoundationBlockEntities.SINK.get(), SinkRenderer::new);
        BlockEntityRendererRegistry.register(FoundationBlockEntities.STACK.get(), StackRenderer::new);
    }

    /** Overlay + tooltip hooks. */
    public static void onInitializeClient() {
        BlockInfoOverlay.init();
        ArmorSetTooltips.init();
        FurnitureColors.init();
        InfoTooltip.init();
        FoundationRarities.init();
    }
}
