package net.nimbu.scabbards;

import com.mojang.logging.LogUtils;
import net.moonlitmistletoe.morrow.Morrow;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.nimbu.scabbards.component.ModDataComponents;
import net.nimbu.scabbards.config.ScabbardConfig;
import net.nimbu.scabbards.config.ScabbardItemCache;
import net.nimbu.scabbards.item.ModItems;
import net.nimbu.scabbards.networking.ModNetworking;
import org.slf4j.Logger;

public class Scabbards {

    public static final String MOD_ID = Morrow.MOD_ID;
    public static final Logger LOGGER = LogUtils.getLogger();

    public Scabbards(IEventBus modEventBus, ModContainer modContainer) {

        // Register common setup
        modEventBus.addListener(this::commonSetup);

        // Register server/game events
        NeoForge.EVENT_BUS.register(this);

        // Register scabbard items
        ModItems.register(modEventBus);

        // Register data components
        ModDataComponents.register(modEventBus);

        // Register config
        modEventBus.addListener(this::onConfigReload);
        modContainer.registerConfig(ModConfig.Type.SERVER, ScabbardConfig.SPEC);

        // Register networking
        modEventBus.addListener(ModNetworking::register);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        ScabbardItemCache.reload();
    }

    private void onConfigReload(ModConfigEvent.Reloading event) {
        ScabbardItemCache.reload();
    }
}