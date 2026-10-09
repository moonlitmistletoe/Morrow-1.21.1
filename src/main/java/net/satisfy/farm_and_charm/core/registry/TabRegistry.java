package net.satisfy.farm_and_charm.core.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.satisfy.farm_and_charm.FarmAndCharm;

public class TabRegistry {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(FarmAndCharm.MOD_ID, Registries.CREATIVE_MODE_TAB);

    @SuppressWarnings("unused")
    public static final RegistrySupplier<CreativeModeTab> FARM_AND_CHARM_TAB = CREATIVE_MODE_TABS.register("farm_and_charm", () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
            .icon(() -> new ItemStack(ObjectRegistry.SUPPLY_CART.get()))
            .title(Component.translatable("creativetab.farm_and_charm.tab"))
            .displayItems((parameters, output) -> {
                output.accept(ObjectRegistry.KERNELS.get());
                output.accept(ObjectRegistry.CORN.get());
                output.accept(ObjectRegistry.OAT_SEEDS.get());
                output.accept(ObjectRegistry.OAT.get());
                output.accept(ObjectRegistry.BARLEY_SEEDS.get());
                output.accept(ObjectRegistry.BARLEY.get());
                output.accept(ObjectRegistry.LETTUCE.get());
                output.accept(ObjectRegistry.ONION.get());
                output.accept(ObjectRegistry.WILD_NETTLE.get());
                output.accept(ObjectRegistry.WILD_EMMER.get());
                output.accept(ObjectRegistry.WILD_BEETROOTS.get());
                output.accept(ObjectRegistry.WILD_POTATOES.get());
                output.accept(ObjectRegistry.WILD_CARROTS.get());
                output.accept(ObjectRegistry.CARROT_BAG.get());
                output.accept(ObjectRegistry.POTATO_BAG.get());
                output.accept(ObjectRegistry.BEETROOT_BAG.get());
                output.accept(ObjectRegistry.STOVE.get());
                output.accept(ObjectRegistry.WATER_TROUGH.get());
                output.accept(ObjectRegistry.FEEDING_TROUGH.get());
                output.accept(ObjectRegistry.WATER_SPRINKLER.get());
                output.accept(ObjectRegistry.CHICKEN_COOP_ITEM.get());
                output.accept(ObjectRegistry.SUPPLY_CART.get());
                output.accept(ObjectRegistry.PLOW.get());
                output.accept(ObjectRegistry.TOOL_RACK.get());
                output.accept(ObjectRegistry.CRAFTING_BOWL.get());
                output.accept(ObjectRegistry.COOKING_POT.get());
                output.accept(ObjectRegistry.POTATO_SOUP.get());
                output.accept(ObjectRegistry.STRAWBERRY_TEA.get());
                output.accept(ObjectRegistry.NETTLE_TEA.get());
                output.accept(ObjectRegistry.STRAWBERRY_TEA_CUP.get());
                output.accept(ObjectRegistry.NETTLE_TEA_CUP.get());
                output.accept(ObjectRegistry.CAT_FOOD.get());
                output.accept(ObjectRegistry.DOG_FOOD.get());
                output.accept(ObjectRegistry.CHICKEN_FEED.get());
                output.accept(ObjectRegistry.HORSE_FODDER.get());
                output.accept(ObjectRegistry.PET_BOWL.get());
                output.accept(ObjectRegistry.DOG_FOOD_BAG.get());
                output.accept(ObjectRegistry.CAT_FOOD_BAG.get());
                output.accept(ObjectRegistry.CHICKEN_NEST.get());
                output.accept(ObjectRegistry.STURDY_LADDER.get());
                output.accept(ObjectRegistry.DUNGAREES.get());
            })
            .build());

    public static void init() {
        CREATIVE_MODE_TABS.register();
    }
}
