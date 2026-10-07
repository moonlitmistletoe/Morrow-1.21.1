package net.satisfy.foundation.client.armor;

import dev.architectury.registry.client.rendering.ColorHandlerRegistry;
import net.minecraft.world.item.Item;
import net.satisfy.foundation.armor.DyeableArmorItem;

public final class ArmorColors {
    private ArmorColors() {
    }

    public static void register(Item... items) {
        ColorHandlerRegistry.registerItemColors((stack, tintIndex) -> {
            if (tintIndex == 0 && stack.getItem() instanceof DyeableArmorItem dyeable) {
                return 0xFF000000 | dyeable.getColor(stack);
            }
            return 0xFFFFFFFF;
        }, items);
    }
}
