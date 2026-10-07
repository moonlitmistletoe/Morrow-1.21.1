package net.satisfy.foundation.client.gui.recipebook;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.satisfy.foundation.recipe.book.StationRecipeBookMenu;

/**
 * Container screen with the green recipe book button, like the crafting table.
 * Extend it instead of {@link net.minecraft.client.gui.screens.inventory.AbstractContainerScreen}
 * and the book works without any further calls.
 */
public abstract class StationRecipeBookScreen<M extends AbstractContainerMenu & StationRecipeBookMenu> extends net.minecraft.client.gui.screens.inventory.AbstractContainerScreen<M> {
    private static final WidgetSprites BUTTON_SPRITES = new WidgetSprites(
            ResourceLocation.withDefaultNamespace("recipe_book/button"),
            ResourceLocation.withDefaultNamespace("recipe_book/button_highlighted"));

    protected final StationRecipeBook recipeBook = new StationRecipeBook();
    private ImageButton recipeBookButton;

    protected StationRecipeBookScreen(M menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        recipeBook.init(width, height, minecraft, menu);
        leftPos = recipeBook.updateScreenPosition(width, imageWidth);
        recipeBookButton = addRenderableWidget(new ImageButton(leftPos + recipeBookButtonX(), topPos + recipeBookButtonY(), 20, 18, BUTTON_SPRITES, button -> {
            recipeBook.toggle();
            leftPos = recipeBook.updateScreenPosition(width, imageWidth);
            button.setPosition(leftPos + recipeBookButtonX(), topPos + recipeBookButtonY());
        }));
    }

    /** Button position relative to the screen, vanilla crafting table spot by default. */
    protected int recipeBookButtonX() {
        return 5;
    }

    protected int recipeBookButtonY() {
        return imageHeight / 2 - 49;
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        recipeBook.tick();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        recipeBook.renderGhost(graphics, leftPos, topPos, partialTick);
        recipeBook.render(graphics, mouseX, mouseY, partialTick);
        recipeBook.renderTooltip(graphics, mouseX, mouseY);
        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (recipeBook.mouseClicked(mouseX, mouseY, button)) {
            setFocused(null);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return recipeBook.keyPressed(keyCode, scanCode, modifiers) || super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        return recipeBook.charTyped(codePoint, modifiers) || super.charTyped(codePoint, modifiers);
    }

    @Override
    protected boolean isHovering(int x, int y, int width, int height, double mouseX, double mouseY) {
        return !recipeBook.isMouseOver(mouseX, mouseY) && super.isHovering(x, y, width, height, mouseX, mouseY);
    }

    @Override
    protected boolean hasClickedOutside(double mouseX, double mouseY, int guiLeft, int guiTop, int mouseButton) {
        return super.hasClickedOutside(mouseX, mouseY, guiLeft, guiTop, mouseButton) && !recipeBook.isMouseOver(mouseX, mouseY);
    }

    @Override
    protected void slotClicked(Slot slot, int slotId, int mouseButton, ClickType type) {
        super.slotClicked(slot, slotId, mouseButton, type);
        recipeBook.clearGhost();
    }
}
