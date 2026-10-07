package net.satisfy.foundation.client.wood;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.ChestBoatModel;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.model.WaterPatchModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.Boat;
import net.satisfy.foundation.wood.BoatWood;
import net.satisfy.foundation.wood.WoodBoat;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

import java.util.HashMap;
import java.util.Map;

public class WoodBoatRenderer extends EntityRenderer<WoodBoat> {
    private final EntityRendererProvider.Context context;
    private final boolean chest;
    private final Map<BoatWood, ListModel<Boat>> models = new HashMap<>();

    public WoodBoatRenderer(EntityRendererProvider.Context context, boolean chest) {
        super(context);
        this.context = context;
        this.chest = chest;
        this.shadowRadius = 0.8F;
    }

    public static ModelLayerLocation layer(BoatWood wood, boolean chest) {
        return new ModelLayerLocation(wood.modelName(chest), "main");
    }

    private ListModel<Boat> model(BoatWood wood) {
        return models.computeIfAbsent(wood, key -> {
            ModelPart part = context.bakeLayer(layer(key, chest));
            return chest ? new ChestBoatModel(part) : new BoatModel(part);
        });
    }

    @Override
    public void render(WoodBoat boat, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0.0F, 0.375F, 0.0F);
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - entityYaw));
        float hurtTime = boat.getHurtTime() - partialTicks;
        float damage = Math.max(boat.getDamage() - partialTicks, 0.0F);
        if (hurtTime > 0.0F) {
            poseStack.mulPose(Axis.XP.rotationDegrees(Mth.sin(hurtTime) * hurtTime * damage / 10.0F * boat.getHurtDir()));
        }
        float bubbleAngle = boat.getBubbleAngle(partialTicks);
        if (!Mth.equal(bubbleAngle, 0.0F)) {
            poseStack.mulPose(new Quaternionf().setAngleAxis(bubbleAngle * Mth.DEG_TO_RAD, 1.0F, 0.0F, 1.0F));
        }

        BoatWood wood = boat.getWood();
        ListModel<Boat> model = model(wood);
        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
        model.setupAnim(boat, partialTicks, 0.0F, -0.1F, 0.0F, 0.0F);
        VertexConsumer consumer = buffer.getBuffer(model.renderType(wood.texture(chest)));
        model.renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);
        if (!boat.isUnderWater() && model instanceof WaterPatchModel waterPatch) {
            waterPatch.waterPatch().render(poseStack, buffer.getBuffer(RenderType.waterMask()), packedLight, OverlayTexture.NO_OVERLAY);
        }
        poseStack.popPose();
        super.render(boat, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(WoodBoat boat) {
        return boat.getWood().texture(chest);
    }
}
