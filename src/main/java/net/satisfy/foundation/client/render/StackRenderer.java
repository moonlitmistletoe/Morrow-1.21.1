package net.satisfy.foundation.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.foundation.block.StackBlockEntity;
import net.satisfy.foundation.block.StackableBlock;

public class StackRenderer implements BlockEntityRenderer<StackBlockEntity> {
    private final BlockRenderDispatcher blockRenderer;

    public StackRenderer(BlockEntityRendererProvider.Context context) {
        this.blockRenderer = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(StackBlockEntity entity, float partialTick, PoseStack poseStack, MultiBufferSource buffers, int light, int overlay) {
        BlockState state = entity.getBlockState();
        if (!state.hasProperty(StackableBlock.ANIMATED)) {
            return;
        }
        poseStack.pushPose();
        WobbleAnimation.apply(poseStack, entity.getLevel(), entity.getWobbleStart(), partialTick, WobbleAnimation.seed(entity.getBlockPos().asLong()));
        blockRenderer.renderSingleBlock(state.setValue(StackableBlock.ANIMATED, false), poseStack, buffers, light, overlay);
        poseStack.popPose();
    }
}
