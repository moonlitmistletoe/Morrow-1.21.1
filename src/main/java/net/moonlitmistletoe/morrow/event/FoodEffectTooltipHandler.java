package net.moonlitmistletoe.morrow.event;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.moonlitmistletoe.morrow.Morrow;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.util.List;

public class FoodEffectTooltipHandler {

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {

        ItemStack stack = event.getItemStack();

        ResourceLocation itemId =
                BuiltInRegistries.ITEM.getKey(stack.getItem());

        // Only add these tooltips to items belonging to Morrow.
        if (!Morrow.MOD_ID.equals(itemId.getNamespace())) {
            return;
        }

        FoodProperties food =
                stack.get(DataComponents.FOOD);

        if (food == null || food.effects().isEmpty()) {
            return;
        }

        List<MobEffectInstance> effects =
                food.effects()
                        .stream()
                        .map(FoodProperties.PossibleEffect::effect)
                        .toList();

        /*
         * Use Minecraft's normal potion tooltip renderer.
         *
         * This automatically gives us tooltips such as:
         *
         * Speed (00:15)
         *
         * When Applied:
         * +20% Speed
         */
        PotionContents.addPotionTooltip(
                effects,
                event.getToolTip()::add,
                1.0F,
                20.0F
        );

        /*
         * Food effects can have a chance of occurring, unlike normal
         * potion effects. Show the probability so the tooltip does not
         * imply that a 25% effect is guaranteed.
         */
        for (FoodProperties.PossibleEffect possibleEffect : food.effects()) {

            float probability = possibleEffect.probability();

            if (probability < 1.0F) {

                int percentage =
                        Math.round(probability * 100.0F);

                event.getToolTip().add(
                        Component.literal(
                                percentage + "% chance"
                        ).withColor(0x808080)
                );
            }
        }
    }
}