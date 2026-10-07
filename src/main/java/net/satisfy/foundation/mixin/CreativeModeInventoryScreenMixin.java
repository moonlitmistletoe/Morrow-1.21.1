package net.satisfy.foundation.mixin;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.satisfy.foundation.client.creative.CreativeSideTabs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(CreativeModeInventoryScreen.class)
public abstract class CreativeModeInventoryScreenMixin extends AbstractContainerScreen<CreativeModeInventoryScreen.ItemPickerMenu> {
    @Unique
    private static final ResourceLocation foundation$TAB_SPRITE = ResourceLocation.withDefaultNamespace("advancements/tab_left_middle");
    @Unique
    private static final ResourceLocation foundation$SELECTED_TAB_SPRITE = ResourceLocation.withDefaultNamespace("advancements/tab_left_middle_selected");
    @Unique
    private static final int foundation$TAB_WIDTH = 32;
    @Unique
    private static final int foundation$TAB_HEIGHT = 28;

    @Shadow
    private static CreativeModeTab selectedTab;

    @Shadow
    private float scrollOffs;

    private CreativeModeInventoryScreenMixin(CreativeModeInventoryScreen.ItemPickerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Inject(method = "selectTab", at = @At("TAIL"))
    private void foundation$filterSideTab(CreativeModeTab tab, CallbackInfo ci) {
        foundation$applySideTab();
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void foundation$clickSideTab(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        List<CreativeSideTabs.SideTab> sideTabs = CreativeSideTabs.get(selectedTab);
        for (int i = 0; i < sideTabs.size(); i++) {
            if (foundation$isOverTab(i, mouseX, mouseY)) {
                CreativeSideTabs.select(selectedTab, i);
                foundation$applySideTab();
                cir.setReturnValue(true);
                return;
            }
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void foundation$renderSideTabs(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        List<CreativeSideTabs.SideTab> sideTabs = CreativeSideTabs.get(selectedTab);
        int selected = CreativeSideTabs.getSelected(selectedTab);
        for (int i = 0; i < sideTabs.size(); i++) {
            int x = foundation$tabX();
            int y = foundation$tabY(i);
            graphics.blitSprite(i == selected ? foundation$SELECTED_TAB_SPRITE : foundation$TAB_SPRITE, x, y, foundation$TAB_WIDTH, foundation$TAB_HEIGHT);
            graphics.renderItem(sideTabs.get(i).icon(), x + 10, y + 6);
        }
        for (int i = 0; i < sideTabs.size(); i++) {
            if (foundation$isOverTab(i, mouseX, mouseY)) {
                graphics.renderTooltip(font, sideTabs.get(i).title(), mouseX, mouseY);
            }
        }
    }

    @Unique
    private void foundation$applySideTab() {
        List<CreativeSideTabs.SideTab> sideTabs = CreativeSideTabs.get(selectedTab);
        if (sideTabs.isEmpty()) {
            return;
        }
        int selected = Math.min(CreativeSideTabs.getSelected(selectedTab), sideTabs.size() - 1);
        CreativeSideTabs.SideTab sideTab = sideTabs.get(selected);
        menu.items.clear();
        for (ItemStack stack : selectedTab.getDisplayItems()) {
            if (sideTab.contains(stack)) {
                menu.items.add(stack);
            }
        }
        scrollOffs = 0.0F;
        menu.scrollTo(0.0F);
    }

    @Unique
    private int foundation$tabX() {
        return leftPos - foundation$TAB_WIDTH + 4;
    }

    @Unique
    private int foundation$tabY(int index) {
        return topPos + 4 + index * foundation$TAB_HEIGHT;
    }

    @Unique
    private boolean foundation$isOverTab(int index, double mouseX, double mouseY) {
        int x = foundation$tabX();
        int y = foundation$tabY(index);
        return mouseX >= x && mouseX < x + foundation$TAB_WIDTH - 4 && mouseY >= y && mouseY < y + foundation$TAB_HEIGHT;
    }
}
