package net.satisfy.foundation.rarity;

import dev.architectury.event.events.client.ClientTooltipEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Item to {@link FoundationRarity} lookup. Any mod using Foundation can register its items here,
 * the tooltip title is then recolored on the client.
 */
public final class FoundationRarities {
    private static final Map<Item, FoundationRarity> RARITIES = new HashMap<>();
    private static boolean initialized;

    private FoundationRarities() {
    }

    public static void register(ItemLike item, FoundationRarity rarity) {
        RARITIES.put(item.asItem(), rarity);
    }

    @Nullable
    public static FoundationRarity get(ItemStack stack) {
        return RARITIES.get(stack.getItem());
    }

    /** Client tooltip hook, replaces the first line (the item name) with the rarity colored one. */
    public static void init() {
        if (initialized) {
            return;
        }
        initialized = true;
        ClientTooltipEvent.ITEM.register((stack, lines, context, flag) -> {
            FoundationRarity rarity = get(stack);
            if (rarity != null && !lines.isEmpty()) {
                Component name = lines.getFirst();
                lines.set(0, rarity.apply(name));
            }
        });
    }
}
