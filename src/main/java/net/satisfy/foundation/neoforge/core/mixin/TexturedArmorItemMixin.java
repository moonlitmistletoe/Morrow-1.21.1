package net.satisfy.foundation.neoforge.core.mixin;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.satisfy.foundation.armor.TexturedArmorItem;
import net.satisfy.foundation.client.armor.ArmorModels;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(TexturedArmorItem.class)
public abstract class TexturedArmorItemMixin extends ArmorItem {
    private TexturedArmorItemMixin(Holder<ArmorMaterial> material, Type type, Properties properties) {
        super(material, type, properties);
    }

    @Override
    public @Nullable ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, ArmorMaterial.Layer layer, boolean innerModel) {
        if (ArmorModels.has(stack.getItem())) {
            return ArmorModels.getTexture(stack.getItem());
        }
        return super.getArmorTexture(stack, entity, slot, layer, innerModel);
    }
}
