package net.satisfy.foundation.armor;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import org.jetbrains.annotations.NotNull;

public class TexturedArmorItem extends ArmorItem {
    private final ResourceLocation texture;

    public TexturedArmorItem(Holder<ArmorMaterial> material, Type type, Properties properties, ResourceLocation texture) {
        super(material, type, properties);
        this.texture = texture;
    }

    public ResourceLocation getTexture() {
        return texture;
    }

    @Override
    public @NotNull EquipmentSlot getEquipmentSlot() {
        return type.getSlot();
    }
}
