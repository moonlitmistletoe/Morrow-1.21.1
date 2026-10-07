package net.satisfy.foundation.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.satisfy.foundation.block.WallDecorationBlock;
import net.satisfy.foundation.block.WallDecorationBlockEntity;
import net.satisfy.foundation.text.SetTextPacket;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class WallDecorationEditScreen extends Screen {
    private static final int IMAGE_SIZE = 128;
    private static final int TEXT_COLOR = 0xFADFB0;

    private final WallDecorationBlockEntity entity;
    private final ResourceLocation texture;
    private final int maxLength;
    private EditBox textField;

    private WallDecorationEditScreen(WallDecorationBlockEntity entity, ResourceLocation texture, int maxLength) {
        super(Component.translatable("gui.foundation.wall_decoration.edit"));
        this.entity = entity;
        this.texture = texture;
        this.maxLength = maxLength;
    }

    public static void open(WallDecorationBlockEntity entity) {
        if (entity.getBlockState().getBlock() instanceof WallDecorationBlock block) {
            Minecraft.getInstance().setScreen(new WallDecorationEditScreen(entity, block.getEditTexture(), block.getMaxLength()));
        }
    }

    @Override
    protected void init() {
        textField = new EditBox(font, width / 2 - 18, height / 2 - 23, 200, 20, Component.empty());
        textField.setMaxLength(maxLength);
        textField.setValue(entity.getText(0).getString());
        textField.setBordered(false);
        textField.setTextColor(TEXT_COLOR);
        addRenderableWidget(textField);
        setInitialFocus(textField);
        addRenderableWidget(Button.builder(Component.translatable("gui.done"), button -> onClose())
                .bounds(width / 2 - 50, height - 40, 100, 20)
                .build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderMenuBackground(graphics);
        graphics.blit(texture, width / 2 - 65, height / 2 - 90, IMAGE_SIZE, IMAGE_SIZE, 0, 0, 16, 16, 16, 16);
        super.render(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(font, title, width / 2, 20, 0xFFFFFF);
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void onClose() {
        SetTextPacket.sendToServer(new SetTextPacket(entity.getBlockPos(), List.of(textField.getValue())));
        super.onClose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
