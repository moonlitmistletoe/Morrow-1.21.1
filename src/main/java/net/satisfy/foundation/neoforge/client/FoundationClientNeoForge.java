package net.satisfy.foundation.neoforge.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.satisfy.foundation.Foundation;
import net.satisfy.foundation.client.FoundationClient;
import net.satisfy.foundation.client.render.SinkRenderer;
import net.satisfy.foundation.client.render.StackRenderer;
import net.satisfy.foundation.registry.FoundationBlockEntities;
import net.satisfy.foundation.particle.DriftingParticle;
import net.satisfy.foundation.particle.ColoredDripParticle;
import net.satisfy.foundation.particle.DyeSplashParticle;
import net.satisfy.foundation.particle.FeatherParticle;
import net.satisfy.foundation.particle.FireflyParticle;
import net.satisfy.foundation.particle.SoupBubbleParticle;
import net.satisfy.foundation.particle.SoupCookingBubbleParticle;
import net.satisfy.foundation.particle.SoupSteamParticle;
import net.satisfy.foundation.particle.WaterDripParticle;
import net.satisfy.foundation.particle.WaterSplashParticle;
import net.satisfy.foundation.registry.FoundationParticles;

@EventBusSubscriber(modid = Foundation.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class FoundationClientNeoForge {
    private static boolean preInitialized;

    @SubscribeEvent
    public static void beforeClientSetup(RegisterEvent event) {
        if (!preInitialized) {
            preInitialized = true;
            FoundationClient.preInitClient();
        }
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(FoundationClient::onInitializeClient);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(FoundationBlockEntities.SINK.get(), SinkRenderer::new);
        event.registerBlockEntityRenderer(FoundationBlockEntities.STACK.get(), StackRenderer::new);
    }

    @SubscribeEvent
    public static void registerParticles(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(FoundationParticles.SOUP_BUBBLE.get(), SoupBubbleParticle.Provider::new);
        event.registerSpriteSet(FoundationParticles.SOUP_STEAM.get(), SoupSteamParticle.Provider::new);
        event.registerSpriteSet(FoundationParticles.COLORED_STEAM.get(), SoupSteamParticle.ColoredProvider::new);
        event.registerSpriteSet(FoundationParticles.SOUP_COOKING_BUBBLE.get(), SoupCookingBubbleParticle.Provider::new);
        event.registerSpriteSet(FoundationParticles.COLORED_SOUP_BUBBLE.get(), SoupBubbleParticle.ColoredProvider::new);
        event.registerSpriteSet(FoundationParticles.COLORED_SOUP_COOKING_BUBBLE.get(), SoupCookingBubbleParticle.ColoredProvider::new);
        event.registerSpriteSet(FoundationParticles.DYE_SPLASH.get(), DyeSplashParticle.Provider::new);
        event.registerSpriteSet(FoundationParticles.FEATHER.get(), FeatherParticle.Provider::new);
        event.registerSpriteSet(FoundationParticles.COLORED_DRIP.get(), ColoredDripParticle.Provider::new);
        event.registerSpriteSet(FoundationParticles.WATER_DRIP.get(), WaterDripParticle.Provider::new);
        event.registerSpriteSet(FoundationParticles.WATER_SPLASH.get(), WaterSplashParticle.Provider::new);
        event.registerSpriteSet(FoundationParticles.FIREFLY.get(), FireflyParticle.Provider::new);
        event.registerSpriteSet(FoundationParticles.LEAF.get(), sprites -> new DriftingParticle.Provider(sprites, DriftingParticle.Style.LEAF, DriftingParticle.MotionProfile.LEAF));
    }
}
