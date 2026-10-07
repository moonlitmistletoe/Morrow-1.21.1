package net.satisfy.foundation.armor;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.DyedItemColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DyeableArmorItem extends ArmorItem {
    private final ResourceLocation texture;
    private final ResourceLocation overlayTexture;
    private final int defaultColor;
    private final ResourceLocation normalizedTexture;
    private final ResourceLocation normalizedOverlayTexture;

    public DyeableArmorItem(Holder<ArmorMaterial> armorMaterial, Type type, int color, Properties properties, ResourceLocation texture) {
        super(armorMaterial, type, properties);
        this.defaultColor = color;
        this.texture = texture;
        this.overlayTexture = null;
        this.normalizedTexture = normalize(texture);
        this.normalizedOverlayTexture = null;
    }

    public DyeableArmorItem(Holder<ArmorMaterial> armorMaterial, Type type, int color, Properties properties, ResourceLocation texture, ResourceLocation overlayTexture) {
        super(armorMaterial, type, properties);
        this.defaultColor = color;
        this.texture = texture;
        this.overlayTexture = overlayTexture;
        this.normalizedTexture = normalize(texture);
        this.normalizedOverlayTexture = normalize(overlayTexture);
    }

    public DyeableArmorItem(ArmorMaterial armorMaterial, Type type, int color, Properties properties, ResourceLocation texture) {
        this(BuiltInRegistries.ARMOR_MATERIAL.wrapAsHolder(armorMaterial), type, color, properties, texture);
    }

    public DyeableArmorItem(ArmorMaterial armorMaterial, Type type, int color, Properties properties, ResourceLocation texture, ResourceLocation overlayTexture) {
        this(BuiltInRegistries.ARMOR_MATERIAL.wrapAsHolder(armorMaterial), type, color, properties, texture, overlayTexture);
    }

    public int getDefaultColor() {
        return defaultColor;
    }

    public int getColor(ItemStack stack) {
        DyedItemColor dyed = stack.get(DataComponents.DYED_COLOR);
        if (dyed != null) return dyed.rgb();
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getCompound("display");
        if (tag.contains("color", 99)) return tag.getInt("color");
        return defaultColor;
    }

    public ResourceLocation getTexture() {
        return normalizedTexture;
    }

    @Nullable
    public ResourceLocation getOverlayTexture() {
        return normalizedOverlayTexture;
    }

    @Override
    public @NotNull EquipmentSlot getEquipmentSlot() {
        return this.type.getSlot();
    }

    private static ResourceLocation normalize(ResourceLocation loc) {
        String path = loc.getPath();
        if (!path.startsWith("textures/")) path = "textures/" + path;
        if (!path.endsWith(".png")) path = path + ".png";
        return ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), path);
    }
}
