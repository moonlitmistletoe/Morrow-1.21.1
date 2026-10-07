package net.satisfy.foundation.client.armor;

import dev.architectury.event.events.client.ClientTooltipEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.satisfy.foundation.armor.ArmorSet;
import net.satisfy.foundation.armor.ArmorSets;
import net.satisfy.foundation.armor.Wearing;

import java.util.List;

public final class ArmorSetTooltips {
    private static boolean initialized;

    private ArmorSetTooltips() {
    }

    public static void init() {
        if (initialized) {
            return;
        }
        initialized = true;
        ClientTooltipEvent.ITEM.register((stack, tooltip, context, flag) -> ArmorSets.find(stack.getItem())
                .filter(ArmorSet::isTooltipVisible)
                .ifPresent(set -> append(set, tooltip)));
    }

    private static void append(ArmorSet set, List<Component> tooltip) {
        Player player = Minecraft.getInstance().player;
        List<List<Item>> pieces = set.pieces();
        int worn = player == null ? 0 : set.wornPieces(player);
        tooltip.add(Component.empty());
        tooltip.add(Component.translatable(set.nameKey()).append(" (" + worn + "/" + pieces.size() + ")").withStyle(ChatFormatting.AQUA));
        for (List<Item> piece : pieces) {
            boolean pieceWorn = player != null && Wearing.isWearingAny(player, piece);
            tooltip.add(Component.literal("- [").append(name(piece)).append("]").withStyle(pieceWorn ? ChatFormatting.GREEN : ChatFormatting.GRAY));
        }
        if (set.bonusKey() != null) {
            boolean active = player != null && set.isBonusActive(player);
            tooltip.add(Component.empty());
            tooltip.add(Component.translatable("tooltip.foundation.set_bonus").withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable(set.bonusKey()).withStyle(active ? ChatFormatting.DARK_GREEN : ChatFormatting.GRAY));
        }
    }

    private static Component name(List<Item> piece) {
        MutableComponent name = Component.empty();
        for (int i = 0; i < piece.size(); i++) {
            if (i > 0) name.append(" / ");
            name.append(piece.get(i).getDescription());
        }
        return name;
    }
}
