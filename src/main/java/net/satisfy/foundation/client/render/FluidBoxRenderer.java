package net.satisfy.foundation.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;

public final class FluidBoxRenderer {
    private static final RenderType FLUID = RenderType.entityTranslucent(TextureAtlas.LOCATION_BLOCKS);
    private static final int DEFAULT_WATER_COLOR = 0x3F76E4;

    private FluidBoxRenderer() {
    }

    public static void renderWaterBox(float xMin, float yMin, float zMin, float xMax, float yMax, float zMax, MultiBufferSource buffer, PoseStack poseStack, int light, boolean renderBottom, @Nullable Level level, BlockPos pos) {
        int waterColor = level != null ? level.getBiome(pos).value().getWaterColor() : DEFAULT_WATER_COLOR;
        renderBox(xMin, yMin, zMin, xMax, yMax, zMax, buffer, poseStack, light, renderBottom, (224 << 24) | waterColor);
    }

    public static void renderBox(float xMin, float yMin, float zMin, float xMax, float yMax, float zMax, MultiBufferSource buffer, PoseStack poseStack, int light, boolean renderBottom, int color) {
        VertexConsumer builder = buffer.getBuffer(FLUID);
        TextureAtlasSprite texture = Minecraft.getInstance().getBlockRenderer().getBlockModel(Blocks.WATER.defaultBlockState()).getParticleIcon();
        int blockLight = (light >> 4) & 0xF;
        int fixedLight = (light & 0xF00000) | (blockLight << 4);

        for (Direction side : Direction.values()) {
            if (side == Direction.DOWN && !renderBottom) {
                continue;
            }
            boolean positive = side.getAxisDirection() == Direction.AxisDirection.POSITIVE;
            if (side.getAxis() == Direction.Axis.X) {
                renderFace(side, zMin, yMin, zMax, yMax, positive ? xMax : xMin, builder, poseStack, fixedLight, color, texture);
            } else if (side.getAxis() == Direction.Axis.Z) {
                renderFace(side, xMin, yMin, xMax, yMax, positive ? zMax : zMin, builder, poseStack, fixedLight, color, texture);
            } else {
                renderFace(side, xMin, zMin, xMax, zMax, positive ? yMax : yMin, builder, poseStack, fixedLight, color, texture);
            }
        }
    }

    private static void renderFace(Direction dir, float left, float down, float right, float up, float depth, VertexConsumer builder, PoseStack poseStack, int light, int color, TextureAtlasSprite texture) {
        boolean positive = dir.getAxisDirection() == Direction.AxisDirection.POSITIVE;
        float u1 = texture.getU0();
        float u2 = texture.getU1();
        float v1 = texture.getV0();
        float v2 = texture.getV1();

        if (dir.getAxis() == Direction.Axis.X) {
            putVertex(builder, poseStack, depth, up, positive ? right : left, color, u1, v1, dir, light);
            putVertex(builder, poseStack, depth, down, positive ? right : left, color, u1, v2, dir, light);
            putVertex(builder, poseStack, depth, down, positive ? left : right, color, u2, v2, dir, light);
            putVertex(builder, poseStack, depth, up, positive ? left : right, color, u2, v1, dir, light);
        } else if (dir.getAxis() == Direction.Axis.Z) {
            putVertex(builder, poseStack, positive ? left : right, up, depth, color, u1, v1, dir, light);
            putVertex(builder, poseStack, positive ? left : right, down, depth, color, u1, v2, dir, light);
            putVertex(builder, poseStack, positive ? right : left, down, depth, color, u2, v2, dir, light);
            putVertex(builder, poseStack, positive ? right : left, up, depth, color, u2, v1, dir, light);
        } else {
            putVertex(builder, poseStack, left, depth, positive ? down : up, color, u1, v1, dir, light);
            putVertex(builder, poseStack, left, depth, positive ? up : down, color, u1, v2, dir, light);
            putVertex(builder, poseStack, right, depth, positive ? up : down, color, u2, v2, dir, light);
            putVertex(builder, poseStack, right, depth, positive ? down : up, color, u2, v1, dir, light);
        }
    }

    private static void putVertex(VertexConsumer builder, PoseStack poseStack, float x, float y, float z, int color, float u, float v, Direction face, int light) {
        Vec3i normal = face.getNormal();
        int a = color >> 24 & 0xFF;

        int r = (int) ((color >> 16 & 0xFF) * 0.7F);
        int g = (int) ((color >> 8 & 0xFF) * 0.7F);
        int b = (int) ((color & 0xFF) * 0.9F);
        builder.addVertex(poseStack.last().pose(), x, y, z)
                .setColor(r, g, b, a)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(normal.getX(), normal.getY(), normal.getZ());
    }
}
