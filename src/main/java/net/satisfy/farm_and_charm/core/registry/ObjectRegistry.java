package net.satisfy.farm_and_charm.core.registry;

import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.satisfy.farm_and_charm.FarmAndCharm;
import net.satisfy.farm_and_charm.core.block.*;
import net.satisfy.farm_and_charm.core.block.crops.*;
import net.satisfy.farm_and_charm.core.item.*;
import net.satisfy.farm_and_charm.core.item.food.*;
import net.satisfy.farm_and_charm.core.util.GeneralUtil;
import net.satisfy.farm_and_charm.platform.PlatformHelper;

import java.util.function.Consumer;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public class ObjectRegistry {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(FarmAndCharm.MOD_ID, Registries.ITEM);
    public static final Registrar<Item> ITEM_REGISTRAR = ITEMS.getRegistrar();
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(FarmAndCharm.MOD_ID, Registries.BLOCK);
    public static final Registrar<Block> BLOCK_REGISTRAR = BLOCKS.getRegistrar();

    public static final RegistrySupplier<Item> SUPPLY_CART = registerItem("supply_cart", () -> new SupplyCartItem(getSettings()));
    public static final RegistrySupplier<Item> PLOW = registerItem("plow", () -> new PlowCartItem(getSettings()));
    public static final RegistrySupplier<Item> CHICKEN_PARTS = registerItem("chicken_parts", () -> new Item(getSettings().food(Foods.CHICKEN)));
    public static final RegistrySupplier<Item> STRAWBERRY_TEA_CUP = registerItem("strawberry_tea_cup", () -> new EffectJugItem(getFoodItemSettings(1, 0.05f, MobEffects.DIG_SPEED, 120, true, false), 120, true));
    public static final RegistrySupplier<Item> CAT_FOOD = registerItem("cat_food", () -> new CatFoodItem(getSettings()));
    public static final RegistrySupplier<Item> HORSE_FODDER = registerItem("horse_fodder", () -> new HorseFodderItem(getSettings()));
    public static final RegistrySupplier<Item> DOG_FOOD = registerItem("dog_food", () -> new DogFoodItem(getSettings()));
    public static final RegistrySupplier<Item> CHICKEN_FEED = registerItem("chicken_feed", () -> new ChickenFeedItem(getSettings()));
    public static final RegistrySupplier<Block> WILD_EMMER = registerWithItem("wild_emmer", () -> new TallGrassBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.TALL_GRASS)));\n    public static final RegistrySupplier<Block> WILD_NETTLE = registerWithItem("wild_nettle", () -> new BonemealableFlowerBlock(MobEffects.REGENERATION, 1, BlockBehaviour.Properties.ofFullCopy(Blocks.DANDELION)));
    public static final RegistrySupplier<Block> WILD_CARROTS = registerWithItem("wild_carrots", () -> new TallGrassBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DANDELION)));
    public static final RegistrySupplier<Block> WILD_BEETROOTS = registerWithItem("wild_beetroots", () -> new TallGrassBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DANDELION)));
    public static final RegistrySupplier<Block> WILD_POTATOES = registerWithItem("wild_potatoes", () -> new TallGrassBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DANDELION)));
    public static final RegistrySupplier<Block> STRAWBERRY_BAG = registerWithItem("strawberry_bag", () -> new FacingBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.RED_WOOL)));
    public static final RegistrySupplier<Block> CARROT_BAG = registerWithItem("carrot_bag", () -> new FacingBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.RED_WOOL)));
    public static final RegistrySupplier<Block> POTATO_BAG = registerWithItem("potato_bag", () -> new FacingBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.RED_WOOL)));
    public static final RegistrySupplier<Block> BEETROOT_BAG = registerWithItem("beetroot_bag", () -> new FacingBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.RED_WOOL)));
    public static final RegistrySupplier<Block> FEEDING_TROUGH = registerWithItem("feeding_trough", () -> new FeedingTroughBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).strength(2.0F, 3.0F).sound(SoundType.WOOD)));
    public static final RegistrySupplier<Block> WATER_TROUGH = registerWithItem("water_trough", () -> new WaterTroughBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).strength(2.0F, 3.0F).sound(SoundType.WOOD).randomTicks()));
    public static final RegistrySupplier<Block> WATER_SPRINKLER = registerWithItem("water_sprinkler", () -> new WaterSprinklerBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).noOcclusion()));
    public static final RegistrySupplier<Block> STOVE = registerWithItem("stove", () -> new StoveBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BRICKS).lightLevel(state -> state.getValue(StoveBlock.LIT) ? 13 : 0)));
    public static final RegistrySupplier<Block> CRAFTING_BOWL = registerWithItem("crafting_bowl", () -> new CraftingBowlBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT).instabreak()));
    public static final RegistrySupplier<Block> COOKING_POT = registerWithItem("cooking_pot", () -> new CookingPotBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.FLOWER_POT).noOcclusion()));
    public static final RegistrySupplier<Block> STRAWBERRY_TEA = registerTea("strawberry_tea", () -> new TeaJugBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)), MobEffects.DIG_SPEED, 240);
    public static final RegistrySupplier<Block> NETTLE_TEA = registerTea("nettle_tea", () -> new TeaJugBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS)), MobEffects.HEAL, 0);
    public static final RegistrySupplier<Block> PET_BOWL = registerWithItem("pet_bowl", () -> new PetBowlBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).strength(1.0F).sound(SoundType.WOOD)));
    public static final RegistrySupplier<Block> DOG_FOOD_BAG = registerWithItem("dog_food_bag", () -> new StackableBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CYAN_CARPET), 3));
    public static final RegistrySupplier<Block> CAT_FOOD_BAG = registerWithItem("cat_food_bag", () -> new StackableBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.PINK_CARPET), 3));
    public static final RegistrySupplier<Block> CHICKEN_NEST = registerWithItem("chicken_nest", () -> new ChickenNestBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GRASS_BLOCK).noCollission().instabreak()));
    public static final RegistrySupplier<Item> DUNGAREES = registerItem("dungarees", () -> new DungareesItem(ArmorMaterialRegistry.withTextureNoOverlay(ArmorMaterialRegistry.CLOTH, FarmAndCharm.identifier("models/armor/dungarees")), ArmorItem.Type.LEGGINGS, getSettings().rarity(Rarity.EPIC), FarmAndCharm.identifier("models/armor/dungarees")));
    public static final RegistrySupplier<Block> CHICKEN_COOP = registerWithoutItem("chicken_coop", () -> new ChickenCoopBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).strength(1.0F).sound(SoundType.WOOD)));
    public static final RegistrySupplier<Item> CHICKEN_COOP_ITEM = registerItem("chicken_coop", () -> new ChickenCoopBlockItem(ObjectRegistry.CHICKEN_COOP.get(), getSettings()));
    public static final RegistrySupplier<Block> STURDY_LADDER = registerWithItem("sturdy_ladder", () -> new SturdyLadderBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.LADDER)));
    public static final RegistrySupplier<Block> WHEAT_PILE = registerWithoutItem("wheat_pile", () -> new PileBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_CARPET).sound(SoundType.GRASS).noOcclusion().instabreak(), () -> Items.WHEAT));
    public static final RegistrySupplier<Block> FEATHER_PILE = registerWithoutItem("feather_pile", () -> new PileBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.WHITE_CARPET).sound(SoundType.GRASS).noOcclusion().instabreak(), () -> Items.FEATHER));

    public static void init() {
        ITEMS.register();
        BLOCKS.register();
    }

    public static BlockBehaviour.Properties properties(float strength) {
        return properties(strength, strength);
    }

    public static BlockBehaviour.Properties properties(float breakSpeed, float explosionResist) {
        return BlockBehaviour.Properties.of().strength(breakSpeed, explosionResist);
    }

    private static Item.Properties getSettings(Consumer<Item.Properties> consumer) {
        Item.Properties settings = new Item.Properties();
        consumer.accept(settings);
        return settings;
    }

    static Item.Properties getSettings() {
        return getSettings(settings -> {});
    }

    private static Item.Properties getFoodItemSettings(int nutrition, float saturationMod, Holder<MobEffect> effect, int duration) {
        return new Item.Properties().food(new FoodProperties.Builder()
                .nutrition(nutrition)
                .saturationModifier(saturationMod)
                .effect(new MobEffectInstance(effect, duration), 1.0f)
                .build());
    }

    private static Item.Properties getFoodItemSettings(int nutrition, float saturationMod, ResourceLocation effectId, int duration) {
        return new Item.Properties().food(new FoodProperties.Builder()
                .nutrition(nutrition)
                .saturationModifier(saturationMod)
                .effect(MobEffectRegistry.inst(effectId, duration), 1.0f)
                .build());
    }

    private static RegistrySupplier<Block> registerBlockWithItem(String blockName, String itemName, Supplier<Block> blockSupplier) {
        RegistrySupplier<Block> block = registerWithoutItem(blockName, blockSupplier);
        registerItem(itemName, () -> new EffectBlockItem(block.get(), getFoodItemSettings(7, 0.7f, MobEffectRegistry.SUSTENANCE, 3600)));
        return block;
    }

    private static Item.Properties getFoodItemSettings(int nutrition, float saturationMod, Holder<MobEffect> effect, int duration, boolean alwaysEat, boolean fast) {
        return getSettings().food(createFood(nutrition, saturationMod, effect, duration, alwaysEat, fast));
    }

    private static Item.Properties getFoodItemSettings(int nutrition, float saturationMod, ResourceLocation effectId, int duration, boolean alwaysEat, boolean fast) {
        return getSettings().food(createFood(nutrition, saturationMod, effectId, duration, alwaysEat, fast));
    }

    private static FoodProperties createFood(int nutrition, float saturationMod, Holder<MobEffect> effect, int duration, boolean alwaysEat, boolean fast) {
        FoodProperties.Builder food = new FoodProperties.Builder().nutrition(nutrition).saturationModifier(saturationMod);
        if (alwaysEat) food.alwaysEdible();
        if (fast) food.fast();
        if (effect != null) food.effect(new MobEffectInstance(effect, duration), 1.0f);
        return food.build();
    }

    private static FoodProperties createFood(int nutrition, float saturationMod, ResourceLocation effectId, int duration, boolean alwaysEat, boolean fast) {
        FoodProperties.Builder food = new FoodProperties.Builder().nutrition(nutrition).saturationModifier(saturationMod);
        if (alwaysEat) food.alwaysEdible();
        if (fast) food.fast();
        if (effectId != null) food.effect(MobEffectRegistry.inst(effectId, duration), 1.0f);
        return food.build();
    }

    private static FoodProperties teaFoodComponent(Holder<MobEffect> effect, int duration) {
        FoodProperties.Builder component = new FoodProperties.Builder().nutrition(1).saturationModifier(1).alwaysEdible();
        if (effect != null) component.effect(new MobEffectInstance(effect, duration), 1.0f);
        return component.build();
    }

    private static RegistrySupplier<Block> registerTea(String name, Supplier<Block> blockSupplier, Holder<MobEffect> effect, int duration) {
        RegistrySupplier<Block> toReturn = registerWithoutItem(name, blockSupplier);
        registerItem(name, () -> new TeaJugItem(toReturn.get(), getSettings().food(teaFoodComponent(effect, duration)).craftRemainder(Items.GLASS_BOTTLE)));
        return toReturn;
    }

    private static String registryPath(String path) {
        return java.util.Set.of("tomato","tomato_seeds","tomato_crop","tomato_crop_body","rotten_tomato","strawberry_seeds","strawberry_crop","wild_ribwort","wild_barley","wild_corn","wild_oat","wild_lettuce","wild_tomatoes","wild_strawberries","wild_onions","lettuce_bag","tomato_bag","onion_bag","corn_bag","flour_bag","oat_ball","barley_ball","fertilized_soil","fertilized_farmland","packed_dirt","trampled_packed_dirt","stable_floor","straw_stable_floor","trampled_stable_floor","cattlegrid","iron_divider","chicken_fence","silo_wood","silo_copper","mincer","roaster","pasta_with_onion_sauce","goulash","onion_soup","barley_soup","simple_tomato_soup","oatmeal_with_strawberries","farmer_salad","corn_grits","barley_patties_with_potatoes","beef_patty_with_vegetables","sausage_with_oat_patty","roasted_chicken","roasted_corn","cooked_salmon","cooked_cod","chicken_wrapped_in_bacon","lamb_with_corn","bacon_with_eggs","oat_pancake","oat_pancake_block","farmers_breakfast","farmers_breakfast_block","baked_lamb_ham","baked_lamb_ham_block","potato_with_roast_meat","potato_with_roast_meat_block","stuffed_chicken","stuffed_chicken_block","stuffed_rabbit","stuffed_rabbit_block","grandmothers_strawberry_cake","grandmothers_strawberry_cake_block","ribwort_tea","ribwort_tea_cup","compost","rope","rope_knot","scarecrow","lettuce","lettuce_crop","lettuce_seeds","corn","wild_strawberry_chance","removed_").contains(path) ? "removed_" + path : path;
    }

    public static <T extends Block> RegistrySupplier<T> registerWithItem(String name, Supplier<T> block) {
        return GeneralUtil.registerWithItem(BLOCKS, BLOCK_REGISTRAR, ITEMS, ITEM_REGISTRAR, FarmAndCharm.identifier(registryPath(name)), block);
    }

    public static <T extends Block> RegistrySupplier<T> registerWithoutItem(String path, Supplier<T> block) {
        return GeneralUtil.registerWithoutItem(BLOCKS, BLOCK_REGISTRAR, FarmAndCharm.identifier(registryPath(path)), block);
    }

    public static <T extends Item> RegistrySupplier<T> registerItem(String path, Supplier<T> itemSupplier) {
        return GeneralUtil.registerItem(ITEMS, ITEM_REGISTRAR, FarmAndCharm.identifier(registryPath(path)), itemSupplier);
    }
}