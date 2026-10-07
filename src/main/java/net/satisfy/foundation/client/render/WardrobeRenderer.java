package net.satisfy.foundation.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.satisfy.foundation.block.WardrobeBlock;
import net.satisfy.foundation.block.WardrobeBlockEntity;
import org.jetbrains.annotations.NotNull;

public class WardrobeRenderer implements BlockEntityRenderer<WardrobeBlockEntity>, RenderLayerParent<ArmorStand, HumanoidModel<ArmorStand>> {
    public record Placement(float x, float y, float z, float yaw, float scale) {
    }

    public static final Placement CHEST = new Placement(-0.2F, 1.2F, 0.0F, 90.0F, 1.7F);
    public static final Placement LEGS = new Placement(0.2F, 1.6F, 0.0F, 90.0F, 1.7F);
    public static final Placement FEET = new Placement(0.2F, 1.4F, 0.0F, 45.0F, 1.7F);

    private final HumanoidModel<ArmorStand> baseModel;
    private final HumanoidArmorLayer<ArmorStand, HumanoidModel<ArmorStand>, HumanoidModel<ArmorStand>> armorLayer;
    private ArmorStand dummy;

    public WardrobeRenderer(BlockEntityRendererProvider.Context context) {
        this.baseModel = new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER));
        this.armorLayer = new HumanoidArmorLayer<>(this,
                new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)),
                new HumanoidModel<>(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)),
                Minecraft.getInstance().getModelManager());
    }

    @Override
    public void render(WardrobeBlockEntity wardrobe, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        if (wardrobe.getLevel() == null || wardrobe.getBlockState().getValue(WardrobeBlock.HALF) == DoubleBlockHalf.UPPER) {
            return;
        }
        if (dummy == null || dummy.level() != wardrobe.getLevel()) {
            dummy = new ArmorStand(EntityType.ARMOR_STAND, wardrobe.getLevel());
            dummy.setNoBasePlate(true);
            dummy.setInvisible(true);
        }

        poseStack.pushPose();
        poseStack.translate(0.5, 1.6, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(-wardrobe.getBlockState().getValue(WardrobeBlock.FACING).toYRot() + 180.0F));
        renderPiece(poseStack, buffer, packedLight, partialTicks, wardrobe.getItem(WardrobeBlockEntity.SLOT_CHEST), EquipmentSlot.CHEST, CHEST);
        renderPiece(poseStack, buffer, packedLight, partialTicks, wardrobe.getItem(WardrobeBlockEntity.SLOT_LEGS), EquipmentSlot.LEGS, LEGS);
        renderPiece(poseStack, buffer, packedLight, partialTicks, wardrobe.getItem(WardrobeBlockEntity.SLOT_FEET), EquipmentSlot.FEET, FEET);
        poseStack.popPose();
    }

    private void renderPiece(PoseStack poseStack, MultiBufferSource buffer, int packedLight, float partialTicks, ItemStack stack, EquipmentSlot slot, Placement placement) {
        if (stack.isEmpty()) {
            return;
        }
        dummy.setItemSlot(slot, stack);
        poseStack.pushPose();
        poseStack.translate(placement.x, placement.y, placement.z);
        poseStack.mulPose(Axis.YP.rotationDegrees(placement.yaw));
        poseStack.scale(placement.scale, -placement.scale, -placement.scale);
        armorLayer.render(poseStack, buffer, packedLight, dummy, 0, 0, partialTicks, 0, 0, 0);
        poseStack.popPose();
        dummy.setItemSlot(slot, ItemStack.EMPTY);
    }

    @Override
    public @NotNull HumanoidModel<ArmorStand> getModel() {
        return baseModel;
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(ArmorStand entity) {
        return ResourceLocation.withDefaultNamespace("textures/entity/armorstand/wood.png");
    }
}
