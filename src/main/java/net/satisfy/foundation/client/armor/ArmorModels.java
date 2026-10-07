package net.satisfy.foundation.client.armor;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.ItemLike;
import net.satisfy.foundation.armor.DyeableArmorItem;
import net.satisfy.foundation.armor.TexturedArmorItem;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class ArmorModels {
    private static final Map<Item, Entry> ENTRIES = new HashMap<>();
    private static final Map<ModelLayerLocation, HumanoidModel<?>> BAKED = new HashMap<>();

    private ArmorModels() {
    }

    public static void register(ModelLayerLocation layer, Function<ModelPart, ? extends HumanoidModel<?>> factory, ItemLike... items) {
        Entry entry = new Entry(layer, factory);
        for (ItemLike item : items) {
            ENTRIES.put(item.asItem(), entry);
        }
    }

    public static boolean has(Item item) {
        return ENTRIES.containsKey(item);
    }

    public static boolean isHidden(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        return data != null && data.contains("Visible") && !data.copyTag().getBoolean("Visible");
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static @Nullable HumanoidModel<?> getModel(ItemStack stack, EquipmentSlot slot, HumanoidModel<?> original) {
        Entry entry = ENTRIES.get(stack.getItem());
        if (entry == null || isHidden(stack) || !(stack.getItem() instanceof Equipable equipable) || equipable.getEquipmentSlot() != slot) {
            return null;
        }
        HumanoidModel<?> model = BAKED.computeIfAbsent(entry.layer, layer -> entry.factory.apply(Minecraft.getInstance().getEntityModels().bakeLayer(layer)));
        ((HumanoidModel) original).copyPropertiesTo((HumanoidModel) model);
        return model;
    }

    public static @Nullable ResourceLocation getTexture(Item item) {
        if (item instanceof DyeableArmorItem dyeable) {
            return dyeable.getTexture();
        }
        if (item instanceof TexturedArmorItem textured) {
            ResourceLocation texture = textured.getTexture();
            String path = texture.getPath();
            if (!path.startsWith("textures/")) path = "textures/" + path;
            if (!path.endsWith(".png")) path = path + ".png";
            return texture.withPath(path);
        }
        return null;
    }

    public static int getColor(ItemStack stack) {
        return stack.getItem() instanceof DyeableArmorItem dyeable ? 0xFF000000 | dyeable.getColor(stack) : 0xFFFFFFFF;
    }

    public static void render(PoseStack poseStack, MultiBufferSource buffers, ItemStack stack, EquipmentSlot slot, int light, HumanoidModel<?> original) {
        HumanoidModel<?> model = getModel(stack, slot, original);
        ResourceLocation texture = getTexture(stack.getItem());
        if (model == null || texture == null) {
            return;
        }
        model.renderToBuffer(poseStack, buffers.getBuffer(model.renderType(texture)), light, OverlayTexture.NO_OVERLAY, getColor(stack));
        if (stack.getItem() instanceof DyeableArmorItem dyeable && dyeable.getOverlayTexture() != null) {
            model.renderToBuffer(poseStack, buffers.getBuffer(model.renderType(dyeable.getOverlayTexture())), light, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);
        }
    }

    private record Entry(ModelLayerLocation layer, Function<ModelPart, ? extends HumanoidModel<?>> factory) {
    }
}
