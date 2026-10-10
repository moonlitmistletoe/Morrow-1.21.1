package net.moonlitmistletoe.morrow.event;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.moonlitmistletoe.morrow.item.ModItems;

import java.util.Set;

public class CowRibDropHandler {

    private static final Set<String> RIB_WEAPONS = Set.of(
            "farmersdelight:diamond_knife",
            "farmersdelight:flint_knife",
            "farmersdelight:golden_knife",
            "farmersdelight:iron_knife",
            "farmersdelight:netherite_knife",

            "moredelight:stone_knife",
            "moredelight:wooden_knife",

            "dungeonsdelight:flint_cleaver",
            "dungeonsdelight:diamond_cleaver",
            "dungeonsdelight:golden_cleaver",
            "dungeonsdelight:iron_cleaver",
            "dungeonsdelight:netherite_cleaver",
            "dungeonsdelight:stained_cleaver"
    );

    @SubscribeEvent
    public static void onCowDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Cow cow)) {
            return;
        }

        if (!(event.getSource().getEntity() instanceof Player player)) {
            return;
        }

        ItemStack weapon = player.getMainHandItem();

        if (!isRibWeapon(weapon)) {
            return;
        }

        if (cow.getRandom().nextFloat() >= 0.25f) {
            return;
        }

        ItemStack ribs = new ItemStack(ModItems.RAW_COW_RIBS.get());

        cow.level().addFreshEntity(new ItemEntity(
                cow.level(),
                cow.getX(),
                cow.getY(),
                cow.getZ(),
                ribs
        ));
    }

    private static boolean isRibWeapon(ItemStack stack) {
        Item item = stack.getItem();
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);

        return id != null && RIB_WEAPONS.contains(id.toString());
    }
}