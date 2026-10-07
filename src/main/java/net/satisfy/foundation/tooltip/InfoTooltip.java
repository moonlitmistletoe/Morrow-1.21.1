package net.satisfy.foundation.tooltip;

import dev.architectury.event.events.client.ClientTooltipEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;

/**
 * Builder for extra item tooltips without touching the item class.
 *
 * <pre>{@code
 * InfoTooltip.of(MyItems.JAR).placeable().details(2).register();
 * }</pre>
 * Detail keys are {@code tooltip.<ns>.<item>.info_0..n} and only show while holding shift.
 */
public final class InfoTooltip {
    private static final int TEXT_COLOR = 0xFFD966;
    private static final int KEY_COLOR = 0xFFD700;
    private static final Map<Item, Entry> ENTRIES = new HashMap<>();
    private static boolean initialized;

    private InfoTooltip() {
    }

    public static void init() {
        if (initialized) {
            return;
        }
        initialized = true;
        ClientTooltipEvent.ITEM.register((stack, lines, context, flag) -> {
            Entry entry = ENTRIES.get(stack.getItem());
            if (entry != null && entry.enabled.getAsBoolean()) {
                entry.append(lines);
            }
        });
    }

    /** Starts a tooltip for the item, don't forget {@code register()}. */
    public static Builder of(ItemLike item) {
        return new Builder(item.asItem());
    }

    public static final class Builder {
        private final Item item;
        private final List<String> lines = new ArrayList<>();
        private boolean placeable;
        private int details;
        private BooleanSupplier enabled = () -> true;

        private Builder(Item item) {
            this.item = item;
        }

        /** Adds the "can be placed" line. */
        public Builder placeable() {
            this.placeable = true;
            return this;
        }

        /** Extra grey line, always visible. */
        public Builder line(String translationKey) {
            this.lines.add(translationKey);
            return this;
        }

        /** Amount of shift-detail lines. */
        public Builder details(int count) {
            this.details = count;
            return this;
        }

        /** Only show when this is true, e.g. a config toggle. */
        public Builder when(BooleanSupplier enabled) {
            this.enabled = enabled;
            return this;
        }

        public void register() {
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(this.item);
            ENTRIES.put(this.item, new Entry(this.placeable, List.copyOf(this.lines), this.details, "tooltip." + id.getNamespace() + "." + id.getPath() + ".info_", this.enabled));
        }
    }

    private record Entry(boolean placeable, List<String> lines, int details, String detailPrefix, BooleanSupplier enabled) {
        void append(List<Component> tooltip) {
            if (this.placeable) {
                tooltip.add(Component.translatable("tooltip.foundation.canbeplaced").withStyle(ChatFormatting.GRAY));
            }
            for (String line : this.lines) {
                tooltip.add(Component.translatable(line).withStyle(ChatFormatting.GRAY));
            }
            if (this.details <= 0) {
                return;
            }
            tooltip.add(Component.empty());
            Style text = Style.EMPTY.withColor(TextColor.fromRgb(TEXT_COLOR));
            if (!Screen.hasShiftDown()) {
                Component key = Component.literal("[SHIFT]").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(KEY_COLOR)));
                tooltip.add(Component.translatable("tooltip.foundation.hold_shift", key).withStyle(text));
                return;
            }
            for (int i = 0; i < this.details; i++) {
                if (i > 0) {
                    tooltip.add(Component.empty());
                }
                tooltip.add(Component.translatable(this.detailPrefix + i).withStyle(text));
            }
        }
    }
}
