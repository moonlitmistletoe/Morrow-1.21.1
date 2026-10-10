package net.moonlitmistletoe.morrow.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Items;

public class ModFoodProperties {

    // Raw ingredients

    public static final FoodProperties EGG_YOLK = new FoodProperties.Builder()
            .nutrition(3)
            .saturationModifier(0.3F)
            .build();

    public static final FoodProperties AVOCADO = new FoodProperties.Builder()
            .nutrition(4)
            .saturationModifier(0.5F)
            .build();

    public static final FoodProperties BLACKBERRY = new FoodProperties.Builder()
            .nutrition(2)
            .saturationModifier(0.3F)
            .build();

    public static final FoodProperties BLUEBERRY = new FoodProperties.Builder()
            .nutrition(2)
            .saturationModifier(0.3F)
            .build();

    public static final FoodProperties CHERRY = new FoodProperties.Builder()
            .nutrition(2)
            .saturationModifier(0.3F)
            .build();

    public static final FoodProperties STRAWBERRY = new FoodProperties.Builder()
            .nutrition(2)
            .saturationModifier(0.3F)
            .build();

    public static final FoodProperties RAW_COW_RIBS = new FoodProperties.Builder()
            .nutrition(3)
            .saturationModifier(0.3F)
            .effect(() -> new MobEffectInstance(
                    MobEffects.HUNGER,
                    100,
                    0
            ), 0.35F)
            .build();


    // Drinks

    public static final FoodProperties APPLE_JUICE = new FoodProperties.Builder()
            .nutrition(4)
            .saturationModifier(0.4F)
            .usingConvertsTo(Items.GLASS_BOTTLE)
            .build();

    public static final FoodProperties CHERRY_JUICE = new FoodProperties.Builder()
            .nutrition(4)
            .saturationModifier(0.4F)
            .usingConvertsTo(Items.GLASS_BOTTLE)
            .effect(() -> new MobEffectInstance(
                    MobEffects.MOVEMENT_SPEED,
                    120,
                    0
            ), 0.5F)
            .build();

    public static final FoodProperties COFFEE = new FoodProperties.Builder()
            .nutrition(3)
            .saturationModifier(0.3F)
            .usingConvertsTo(Items.GLASS_BOTTLE)
            .effect(() -> new MobEffectInstance(
                    MobEffects.MOVEMENT_SPEED,
                    300,
                    0
            ), 1.0F)
            .build();

    public static final FoodProperties STRAWBERRY_SMOOTHIE = new FoodProperties.Builder()
            .nutrition(6)
            .saturationModifier(0.6F)
            .usingConvertsTo(Items.GLASS_BOTTLE)
            .effect(() -> new MobEffectInstance(
                    MobEffects.REGENERATION,
                    100,
                    0
            ), 0.75F)
            .build();


    // Prepared foods

    public static final FoodProperties AVOCADO_TOAST = new FoodProperties.Builder()
            .nutrition(7)
            .saturationModifier(0.7F)
            .build();

    public static final FoodProperties BLACKBERRY_JAM = new FoodProperties.Builder()
            .nutrition(4)
            .saturationModifier(0.5F)
            .usingConvertsTo(Items.GLASS_BOTTLE)
            .build();

    public static final FoodProperties BLUEBERRY_JAM = new FoodProperties.Builder()
            .nutrition(4)
            .saturationModifier(0.5F)
            .usingConvertsTo(Items.GLASS_BOTTLE)
            .effect(() -> new MobEffectInstance(
                    MobEffects.NIGHT_VISION,
                    240,
                    0
            ), 0.4F)
            .build();

    public static final FoodProperties CHERRY_JAM = new FoodProperties.Builder()
            .nutrition(4)
            .saturationModifier(0.5F)
            .usingConvertsTo(Items.GLASS_BOTTLE)
            .effect(() -> new MobEffectInstance(
                    MobEffects.LUCK,
                    300,
                    0
            ), 0.4F)
            .build();

    public static final FoodProperties SCRAMBLED_EGGS = new FoodProperties.Builder()
            .nutrition(6)
            .saturationModifier(0.7F)
            .build();

    public static final FoodProperties SUNNY_SIDE_EGGS = new FoodProperties.Builder()
            .nutrition(7)
            .saturationModifier(0.7F)
            .effect(() -> new MobEffectInstance(
                    MobEffects.DIG_SPEED,
                    240,
                    0
            ), 0.6F)
            .build();

    public static final FoodProperties COOKED_COW_RIBS = new FoodProperties.Builder()
            .nutrition(9)
            .saturationModifier(0.8F)
            .effect(() -> new MobEffectInstance(
                    MobEffects.DAMAGE_RESISTANCE,
                    200,
                    0
            ), 0.3F)
            .build();


    // Desserts

    public static final FoodProperties APPLE_PIE_SLICE = new FoodProperties.Builder()
            .nutrition(7)
            .saturationModifier(0.7F)
            .effect(() -> new MobEffectInstance(
                    MobEffects.ABSORPTION,
                    300,
                    0
            ), 0.5F)
            .build();

    public static final FoodProperties CHERRY_PIE = new FoodProperties.Builder()
            .nutrition(7)
            .saturationModifier(0.7F)
            .effect(() -> new MobEffectInstance(
                    MobEffects.LUCK,
                    400,
                    0
            ), 0.5F)
            .build();

    public static final FoodProperties MOONCAKE = new FoodProperties.Builder()
            .nutrition(8)
            .saturationModifier(0.8F)
            .effect(() -> new MobEffectInstance(
                    MobEffects.NIGHT_VISION,
                    600,
                    0
            ), 0.75F)
            .build();

    public static final FoodProperties STRAWBERRY_ICE_CREAM = new FoodProperties.Builder()
            .nutrition(6)
            .saturationModifier(0.6F)
            .effect(() -> new MobEffectInstance(
                    MobEffects.REGENERATION,
                    80,
                    0
            ), 0.6F)
            .build();
}