package net.satisfy.foundation.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.satisfy.foundation.block.WallDecorationBlock;
import net.satisfy.foundation.block.WallDecorationBlockEntity;

public class WallDecorationRenderer implements BlockEntityRenderer<WallDecorationBlockEntity> {
    private static final int TEXT_COLOR = 0xFADFB0;
    private static final int FULL_BRIGHT = 0xF000F0;

    public WallDecorationRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(WallDecorationBlockEntity entity, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay) {
        String text = entity.getText(0).getString();
        if (entity.getBlockState().getBlock() instanceof WallDecorationBlock block && text.length() > block.getMaxLength()) {
            text = text.substring(0, block.getMaxLength());
        }
        if (text.isEmpty()) {
            return;
        }
        Font font = Minecraft.getInstance().font;
        Direction facing = entity.getBlockState().getValue(WallDecorationBlock.FACING);
        float rotation = switch (facing) {
            case NORTH -> 180F;
            case WEST -> -90F;
            case EAST -> 90F;
            default -> 0F;
        };
        poseStack.pushPose();
        poseStack.translate(0.5, 1.0, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
        poseStack.translate(0, -0.45, -0.425);
        poseStack.scale(0.01F, -0.01F, 0.01F);
        float x = -font.width(text) / 2F;
        if (entity.isGlowing()) {
            MultiBufferSource.BufferSource seeThrough = Minecraft.getInstance().renderBuffers().bufferSource();
            font.drawInBatch(Component.literal(text).getVisualOrderText(), x, 0, TEXT_COLOR, false, poseStack.last().pose(), seeThrough, Font.DisplayMode.SEE_THROUGH, 0, FULL_BRIGHT);
            seeThrough.endBatch();
        } else {
            font.drawInBatch(Component.literal(text).getVisualOrderText(), x, 0, TEXT_COLOR, false, poseStack.last().pose(), buffer, Font.DisplayMode.NORMAL, 0, light);
        }
        poseStack.popPose();
    }
}
