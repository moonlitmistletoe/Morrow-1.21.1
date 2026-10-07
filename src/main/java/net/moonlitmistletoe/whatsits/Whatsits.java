package net.moonlitmistletoe.whatsits;

import net.moonlitmistletoe.whatsits.block.ModBlocks;
import net.satisfy.bakery.Bakery;
import net.satisfy.farm_and_charm.FarmAndCharm;
import net.moonlitmistletoe.whatsits.item.ModItems;
import net.nimbu.scabbards.Scabbards;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.moonlitmistletoe.whatsits.event.EggYolkHandler;
import net.moonlitmistletoe.whatsits.event.CropHarvestHandler;
import net.moonlitmistletoe.whatsits.event.CowRibDropHandler;
import net.moonlitmistletoe.whatsits.event.FoodEffectTooltipHandler;
import net.moonlitmistletoe.whatsits.util.HandcuffManager;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@Mod(Whatsits.MOD_ID)
public class Whatsits {

    public static final String MOD_ID = "whatsits";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Whatsits(IEventBus modEventBus, ModContainer modContainer) {

        modEventBus.addListener(this::commonSetup);

        ModSounds.register(modEventBus);

        NeoForge.EVENT_BUS.register(this);

        NeoForge.EVENT_BUS.register(EggYolkHandler.class);
        NeoForge.EVENT_BUS.register(CropHarvestHandler.class);
        NeoForge.EVENT_BUS.register(CowRibDropHandler.class);
        NeoForge.EVENT_BUS.register(FoodEffectTooltipHandler.class);
        NeoForge.EVENT_BUS.register(HandcuffManager.class);

        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);

        new Scabbards(modEventBus, modContainer);

        // Register the merged Farm & Charm content first, since Bakery uses its classes.
        FarmAndCharm.init();

        // Register the merged Bakery food/cake content.
        Bakery.init();

        ModCreativeModeTabs.register(modEventBus);

        // Only Whatsits' own config is registered.
        modContainer.registerConfig(
                ModConfig.Type.COMMON,
                Config.SPEC
        );
    }

    private void commonSetup(FMLCommonSetupEvent event) {
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }

}
