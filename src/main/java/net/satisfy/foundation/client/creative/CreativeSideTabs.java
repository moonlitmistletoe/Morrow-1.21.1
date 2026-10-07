package net.satisfy.foundation.client.creative;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class CreativeSideTabs {
    private static final Map<ResourceKey<CreativeModeTab>, List<SideTab>> SIDE_TABS = new HashMap<>();
    private static final Map<ResourceKey<CreativeModeTab>, Integer> SELECTED = new HashMap<>();

    private CreativeSideTabs() {
    }

    public static void register(ResourceKey<CreativeModeTab> tab, SideTab... sideTabs) {
        SIDE_TABS.put(tab, List.of(sideTabs));
    }

    public static List<SideTab> get(CreativeModeTab tab) {
        Optional<ResourceKey<CreativeModeTab>> key = BuiltInRegistries.CREATIVE_MODE_TAB.getResourceKey(tab);
        return key.map(k -> SIDE_TABS.getOrDefault(k, List.of())).orElse(List.of());
    }

    public static int getSelected(CreativeModeTab tab) {
        return BuiltInRegistries.CREATIVE_MODE_TAB.getResourceKey(tab).map(k -> SELECTED.getOrDefault(k, 0)).orElse(0);
    }

    public static void select(CreativeModeTab tab, int index) {
        BuiltInRegistries.CREATIVE_MODE_TAB.getResourceKey(tab).ifPresent(k -> SELECTED.put(k, index));
    }

    public static final class SideTab {
        private final Component title;
        private final Supplier<ItemStack> icon;
        private final Consumer<CreativeModeTab.Output> contents;
        private Set<Item> items;

        public SideTab(Component title, Supplier<ItemStack> icon, Consumer<CreativeModeTab.Output> contents) {
            this.title = title;
            this.icon = icon;
            this.contents = contents;
        }

        public Component title() {
            return title;
        }

        public ItemStack icon() {
            return icon.get();
        }

        public boolean contains(ItemStack stack) {
            if (items == null) {
                Set<Item> collected = new HashSet<>();
                contents.accept((stack1, visibility) -> collected.add(stack1.getItem()));
                items = collected;
            }
            return items.contains(stack.getItem());
        }

        public static SideTab of(Component title, ItemLike icon, Consumer<CreativeModeTab.Output> contents) {
            return new SideTab(title, () -> new ItemStack(icon), contents);
        }
    }
}
