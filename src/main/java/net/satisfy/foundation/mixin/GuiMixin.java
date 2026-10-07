package net.satisfy.foundation.mixin;

import net.minecraft.client.gui.Gui;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.satisfy.foundation.rarity.FoundationRarities;
import net.satisfy.foundation.rarity.FoundationRarity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/** Colors the held item name above the hotbar with its {@link FoundationRarity}. */
@Mixin(Gui.class)
public abstract class GuiMixin {
    @Redirect(method = "renderSelectedItemName", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getHoverName()Lnet/minecraft/network/chat/Component;"), require = 0)
    private Component foundation$rarityName(ItemStack stack) {
        FoundationRarity rarity = FoundationRarities.get(stack);
        Component name = stack.getHoverName();
        return rarity == null ? name : rarity.apply(name);
    }
}
