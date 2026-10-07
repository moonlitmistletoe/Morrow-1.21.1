package net.satisfy.foundation.client.render;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.Level;
import net.satisfy.foundation.Foundation;
import net.satisfy.foundation.block.SinkBlock;
import net.satisfy.foundation.block.SinkBlockEntity;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SinkRenderer implements BlockEntityRenderer<SinkBlockEntity> {
    private static final ResourceLocation FAUCET_MODEL = Foundation.identifier("models/block/sink_faucet.json");
    private static final ResourceLocation FAUCET_TEXTURE = Foundation.identifier("block/sink_faucet");
    private static final ResourceLocation WATER_TEXTURE = ResourceLocation.withDefaultNamespace("block/water_still");
    private static final float PIXEL = 1.0F / 16.0F;
    private static final float HANDLE_TURN = 135.0F;
    private static List<Element> faucet;

    public SinkRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(SinkBlockEntity sink, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int light, int overlay) {
        Level level = sink.getLevel();
        if (level == null || !(sink.getBlockState().getBlock() instanceof SinkBlock)) {
            return;
        }
        Direction facing = sink.getBlockState().getValue(SinkBlock.FACING);
        float yaw = switch (facing) {
            case EAST -> 90.0F;
            case SOUTH -> 180.0F;
            case WEST -> 270.0F;
            default -> 0.0F;
        };
        poseStack.pushPose();
        poseStack.translate(0.5F, 0.0F, 0.5F);
        poseStack.mulPose(Axis.YP.rotationDegrees(-yaw));
        poseStack.translate(-0.5F, 0.0F, -0.5F);

        if (sink.getWaterLevel() > 0) {
            drawWater(poseStack, buffers, level, sink.getBlockPos(), sink.getWaterLevel(), light);
        }

        float turn = Mth.clamp((level.getGameTime() + partialTick - sink.getToggleTime()) / SinkBlockEntity.TURN_TICKS, 0.0F, 1.0F);
        float eased = 1.0F - (1.0F - turn) * (1.0F - turn);
        float handle = (sink.isOpen() ? eased : 1.0F - eased) * HANDLE_TURN;
        poseStack.translate(0.0F, 1.0F, 0.0F);
        int faucetLight = LevelRenderer.getLightColor(level, sink.getBlockPos().above());
        drawFaucet(poseStack, buffers, handle, faucetLight);
        poseStack.popPose();
    }

    private static void drawWater(PoseStack poseStack, MultiBufferSource buffers, Level level, BlockPos pos, int waterLevel, int light) {
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(WATER_TEXTURE);
        int color = BiomeColors.getAverageWaterColor(level, pos);
        float r = (color >> 16 & 255) / 255.0F, g = (color >> 8 & 255) / 255.0F, b = (color & 255) / 255.0F;
        float y = (12.0F + waterLevel) * PIXEL;
        float x0 = 3 * PIXEL, x1 = 13 * PIXEL, z0 = 3 * PIXEL, z1 = 12 * PIXEL;
        VertexConsumer consumer = buffers.getBuffer(RenderType.translucent());
        PoseStack.Pose pose = poseStack.last();
        float[][] corners = {{x0, z0}, {x0, z1}, {x1, z1}, {x1, z0}};
        for (float[] corner : corners) {
            consumer.addVertex(pose, corner[0], y, corner[1])
                    .setColor(r, g, b, 0.85F)
                    .setUv(sprite.getU(corner[0]), sprite.getV(corner[1]))
                    .setLight(light)
                    .setNormal(pose, 0.0F, 1.0F, 0.0F);
        }
    }

    private static void drawFaucet(PoseStack poseStack, MultiBufferSource buffers, float handleAngle, int light) {
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(FAUCET_TEXTURE);
        VertexConsumer consumer = buffers.getBuffer(RenderType.cutout());
        for (Element element : faucet()) {
            poseStack.pushPose();
            if (element.handle) {
                float cy = (element.from[1] + element.to[1]) / 2.0F * PIXEL;
                float cz = (element.from[2] + element.to[2]) / 2.0F * PIXEL;
                poseStack.translate(0.0F, cy, cz);
                poseStack.mulPose(Axis.XP.rotationDegrees(handleAngle));
                poseStack.translate(0.0F, -cy, -cz);
            }
            element.draw(poseStack.last(), consumer, sprite, light);
            poseStack.popPose();
        }
    }

    private static List<Element> faucet() {
        if (faucet == null) {
            faucet = new ArrayList<>();
            Minecraft.getInstance().getResourceManager().getResource(FAUCET_MODEL).ifPresent(resource -> {
                try (Reader reader = resource.openAsReader()) {
                    JsonObject model = JsonParser.parseReader(reader).getAsJsonObject();
                    for (JsonElement entry : model.getAsJsonArray("elements")) {
                        faucet.add(Element.parse(entry.getAsJsonObject()));
                    }
                } catch (Exception exception) {
                    Foundation.LOGGER.warn("Could not read sink faucet model", exception);
                }
            });
        }
        return faucet;
    }

    private record Face(Direction direction, float[] uv, int rotation) {
    }

    private record Element(float[] from, float[] to, boolean handle, List<Face> faces) {
        static Element parse(JsonObject json) {
            List<Face> faces = new ArrayList<>();
            for (Map.Entry<String, JsonElement> entry : json.getAsJsonObject("faces").entrySet()) {
                JsonObject face = entry.getValue().getAsJsonObject();
                Direction direction = Direction.byName(entry.getKey());
                if (direction != null) {
                    faces.add(new Face(direction, floats(face.getAsJsonArray("uv")), face.has("rotation") ? face.get("rotation").getAsInt() : 0));
                }
            }
            return new Element(floats(json.getAsJsonArray("from")), floats(json.getAsJsonArray("to")), json.has("name") && "handle".equals(json.get("name").getAsString()), faces);
        }

        private static float[] floats(JsonArray array) {
            float[] values = new float[array.size()];
            for (int index = 0; index < values.length; index++) {
                values[index] = array.get(index).getAsFloat();
            }
            return values;
        }

        void draw(PoseStack.Pose pose, VertexConsumer consumer, TextureAtlasSprite sprite, int light) {
            float x0 = from[0] * PIXEL, y0 = from[1] * PIXEL, z0 = from[2] * PIXEL;
            float x1 = to[0] * PIXEL, y1 = to[1] * PIXEL, z1 = to[2] * PIXEL;
            for (Face face : faces) {
                float[][] points = switch (face.direction) {
                    case UP -> new float[][]{{x0, y1, z0}, {x0, y1, z1}, {x1, y1, z1}, {x1, y1, z0}};
                    case DOWN -> new float[][]{{x0, y0, z1}, {x0, y0, z0}, {x1, y0, z0}, {x1, y0, z1}};
                    case NORTH -> new float[][]{{x1, y1, z0}, {x1, y0, z0}, {x0, y0, z0}, {x0, y1, z0}};
                    case SOUTH -> new float[][]{{x0, y1, z1}, {x0, y0, z1}, {x1, y0, z1}, {x1, y1, z1}};
                    case WEST -> new float[][]{{x0, y1, z0}, {x0, y0, z0}, {x0, y0, z1}, {x0, y1, z1}};
                    case EAST -> new float[][]{{x1, y1, z1}, {x1, y0, z1}, {x1, y0, z0}, {x1, y1, z0}};
                };
                float u0 = face.uv[0], v0 = face.uv[1], u1 = face.uv[2], v1 = face.uv[3];
                float[][] uvs = {{u0, v0}, {u0, v1}, {u1, v1}, {u1, v0}};
                int shift = (face.rotation / 90) % 4;
                float shade = switch (face.direction) {
                    case UP -> 1.0F;
                    case DOWN -> 0.5F;
                    case NORTH, SOUTH -> 0.8F;
                    default -> 0.6F;
                };
                for (int corner = 0; corner < 4; corner++) {
                    float[] uv = uvs[(corner + shift) % 4];
                    consumer.addVertex(pose, points[corner][0], points[corner][1], points[corner][2])
                            .setColor(shade, shade, shade, 1.0F)
                            .setUv(sprite.getU(uv[0] / 16.0F), sprite.getV(uv[1] / 16.0F))
                            .setLight(light)
                            .setNormal(pose, face.direction.getStepX(), face.direction.getStepY(), face.direction.getStepZ());
                }
            }
        }
    }
}
