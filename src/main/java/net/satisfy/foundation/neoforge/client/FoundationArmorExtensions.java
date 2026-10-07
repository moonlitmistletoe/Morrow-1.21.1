package net.satisfy.foundation.neoforge.client;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.satisfy.foundation.armor.DyeableArmorItem;
import net.satisfy.foundation.client.armor.ArmorModels;
import org.jetbrains.annotations.NotNull;

public final class FoundationArmorExtensions implements IClientItemExtensions {
    public static final FoundationArmorExtensions INSTANCE = new FoundationArmorExtensions();

    private FoundationArmorExtensions() {
    }

    @Override
    public @NotNull Model getGenericArmorModel(@NotNull LivingEntity entity, @NotNull ItemStack stack, @NotNull EquipmentSlot slot, @NotNull HumanoidModel<?> original) {
        HumanoidModel<?> model = ArmorModels.getModel(stack, slot, original);
        return model != null ? model : original;
    }

    @Override
    public int getArmorLayerTintColor(@NotNull ItemStack stack, @NotNull LivingEntity entity, @NotNull ArmorMaterial.Layer layer, int layerIdx, int fallbackColor) {
        if (ArmorModels.isHidden(stack)) {
            return 0;
        }
        if (stack.getItem() instanceof DyeableArmorItem) {
            return layerIdx == 0 ? ArmorModels.getColor(stack) : 0xFFFFFFFF;
        }
        return fallbackColor;
    }
}
