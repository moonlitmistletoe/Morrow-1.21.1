package net.satisfy.farm_and_charm.core.registry;

import dev.architectury.registry.level.entity.trade.TradeRegistry;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;

public class VillagerTradeRegistryHandler {
    public static void init() {
        registerFarmerTrades();
    }

    private static void registerFarmerTrades() {
        TradeRegistry.registerVillagerTrade(VillagerProfession.FARMER, 1, (entity, random) -> new MerchantOffer(new ItemCost(Items.EMERALD, 16), new ItemStack(ObjectRegistry.DUNGAREES.get(), 1), 2, 2, 0.05f));
    }
}