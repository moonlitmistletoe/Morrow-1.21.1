package net.moonlitmistletoe.whatsits.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.moonlitmistletoe.whatsits.block.ModBlocks;
import net.moonlitmistletoe.whatsits.item.custom.DrinkItem;
import net.moonlitmistletoe.whatsits.item.custom.HandcuffsItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems("whatsits");

    public static final DeferredItem<Item> MOD_LOGO;
    public static final DeferredItem<Item> MANUAL;
    public static final DeferredItem<Item> BLACKBERRY_SEED;
    public static final DeferredItem<Item> BLUEBERRY_SEED;
    public static final DeferredItem<Item> COFFEE_SEED;
    public static final DeferredItem<Item> EGG_YOLK;
    public static final DeferredItem<Item> AVOCADO;
    public static final DeferredItem<Item> BLACKBERRY;
    public static final DeferredItem<Item> BLUEBERRY;
    public static final DeferredItem<Item> STRAWBERRY;
    public static final DeferredItem<Item> CHERRY;
    public static final DeferredItem<Item> COFFEE_BEANS;
    public static final DeferredItem<Item> RAW_COW_RIBS;
    public static final DeferredItem<Item> APPLE_JUICE;
    public static final DeferredItem<Item> CHERRY_JUICE;
    public static final DeferredItem<Item> COFFEE;
    public static final DeferredItem<Item> STRAWBERRY_SMOOTHIE;
    public static final DeferredItem<Item> AVOCADO_TOAST;
    public static final DeferredItem<Item> BLACKBERRY_JAM;
    public static final DeferredItem<Item> BLUEBERRY_JAM;
    public static final DeferredItem<Item> CHERRY_JAM;
    public static final DeferredItem<Item> SCRAMBLED_EGGS;
    public static final DeferredItem<Item> SUNNY_SIDE_EGGS;
    public static final DeferredItem<Item> COOKED_COW_RIBS;
    public static final DeferredItem<Item> CHERRY_PIE;
    public static final DeferredItem<Item> MOONCAKE;
    public static final DeferredItem<Item> STRAWBERRY_ICE_CREAM;
    public static final ResourceKey<JukeboxSong> DORIME_SONG;
    public static final DeferredItem<Item> DORIME;
    public static final ResourceKey<JukeboxSong> WORLD_OF_LIES_SONG;
    public static final DeferredItem<Item> WORLD_OF_LIES;
    public static final DeferredItem<Item> HANDCUFFS;

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

    static {
        MOD_LOGO = ITEMS.register("mod_logo", () -> new Item(new Item.Properties()));
        MANUAL = ITEMS.register("manual", () -> new Item(new Item.Properties()));

        BLACKBERRY_SEED = ITEMS.register(
                "blackberry_seed",
                () -> new ItemNameBlockItem((Block) ModBlocks.BLACKBERRY_CROP.get(), new Item.Properties())
        );

        BLUEBERRY_SEED = ITEMS.register(
                "blueberry_seed",
                () -> new ItemNameBlockItem((Block) ModBlocks.BLUEBERRY_CROP.get(), new Item.Properties())
        );

        COFFEE_SEED = ITEMS.register(
                "coffee_seed",
                () -> new ItemNameBlockItem((Block) ModBlocks.COFFEE_CROP.get(), new Item.Properties())
        );

        EGG_YOLK = ITEMS.register(
                "egg_yolk",
                () -> new Item(new Item.Properties().food(ModFoodProperties.EGG_YOLK))
        );

        AVOCADO = ITEMS.register(
                "avocado",
                () -> new Item(new Item.Properties().food(ModFoodProperties.AVOCADO))
        );

        BLACKBERRY = ITEMS.register(
                "blackberry",
                () -> new Item(new Item.Properties().food(ModFoodProperties.BLACKBERRY))
        );

        BLUEBERRY = ITEMS.register(
                "blueberry",
                () -> new Item(new Item.Properties().food(ModFoodProperties.BLUEBERRY))
        );

        STRAWBERRY = ITEMS.register(
                "strawberry",
                () -> new Item(new Item.Properties().food(net.minecraft.world.food.Foods.SWEET_BERRIES))
        );

        CHERRY = ITEMS.register(
                "cherry",
                () -> new Item(new Item.Properties().food(ModFoodProperties.CHERRY))
        );

        COFFEE_BEANS = ITEMS.register(
                "coffee_beans",
                () -> new Item(new Item.Properties())
        );

        RAW_COW_RIBS = ITEMS.register(
                "raw_cow_ribs",
                () -> new Item(new Item.Properties().food(ModFoodProperties.RAW_COW_RIBS))
        );

        APPLE_JUICE = ITEMS.register(
                "apple_juice",
                () -> new DrinkItem(new Item.Properties().food(ModFoodProperties.APPLE_JUICE))
        );

        CHERRY_JUICE = ITEMS.register(
                "cherry_juice",
                () -> new DrinkItem(new Item.Properties().food(ModFoodProperties.CHERRY_JUICE))
        );

        COFFEE = ITEMS.register(
                "coffee",
                () -> new DrinkItem(new Item.Properties().food(ModFoodProperties.COFFEE))
        );

        STRAWBERRY_SMOOTHIE = ITEMS.register(
                "strawberry_smoothie",
                () -> new DrinkItem(new Item.Properties().food(ModFoodProperties.STRAWBERRY_SMOOTHIE))
        );

        AVOCADO_TOAST = ITEMS.register(
                "avocado_toast",
                () -> new Item(new Item.Properties().food(ModFoodProperties.AVOCADO_TOAST))
        );

        BLACKBERRY_JAM = ITEMS.register(
                "blackberry_jam",
                () -> new DrinkItem(new Item.Properties().food(ModFoodProperties.BLACKBERRY_JAM))
        );

        BLUEBERRY_JAM = ITEMS.register(
                "blueberry_jam",
                () -> new DrinkItem(new Item.Properties().food(ModFoodProperties.BLUEBERRY_JAM))
        );

        CHERRY_JAM = ITEMS.register(
                "cherry_jam",
                () -> new DrinkItem(new Item.Properties().food(ModFoodProperties.CHERRY_JAM))
        );

        SCRAMBLED_EGGS = ITEMS.register(
                "scrambled_eggs",
                () -> new Item(new Item.Properties().food(ModFoodProperties.SCRAMBLED_EGGS))
        );

        SUNNY_SIDE_EGGS = ITEMS.register(
                "sunny_side_eggs",
                () -> new Item(new Item.Properties().food(ModFoodProperties.SUNNY_SIDE_EGGS))
        );

        COOKED_COW_RIBS = ITEMS.register(
                "cooked_cow_ribs",
                () -> new Item(new Item.Properties().food(ModFoodProperties.COOKED_COW_RIBS))
        );

        CHERRY_PIE = ITEMS.register(
                "cherry_pie",
                () -> new Item(new Item.Properties().food(ModFoodProperties.CHERRY_PIE))
        );

        MOONCAKE = ITEMS.register(
                "mooncake",
                () -> new Item(new Item.Properties().food(ModFoodProperties.MOONCAKE))
        );

        STRAWBERRY_ICE_CREAM = ITEMS.register(
                "strawberry_ice_cream",
                () -> new Item(new Item.Properties().food(ModFoodProperties.STRAWBERRY_ICE_CREAM))
        );

        DORIME_SONG = ResourceKey.create(
                Registries.JUKEBOX_SONG,
                ResourceLocation.fromNamespaceAndPath("whatsits", "dorime")
        );

        DORIME = ITEMS.register(
                "dorime",
                () -> new Item(new Item.Properties().rarity(Rarity.RARE).jukeboxPlayable(DORIME_SONG))
        );

        WORLD_OF_LIES_SONG = ResourceKey.create(
                Registries.JUKEBOX_SONG,
                ResourceLocation.fromNamespaceAndPath("whatsits", "worldoflies")
        );

        WORLD_OF_LIES = ITEMS.register(
                "world_of_lies",
                () -> new Item(new Item.Properties().rarity(Rarity.RARE).jukeboxPlayable(WORLD_OF_LIES_SONG))
        );

        HANDCUFFS = ITEMS.register(
                "handcuffs",
                () -> new HandcuffsItem(new Item.Properties())
        );
    }
}