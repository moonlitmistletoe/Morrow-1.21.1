package net.satisfy.foundation.compat.rei;

import me.shedaniel.math.Point;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.satisfy.foundation.compat.RecipeViewerLayout;

import java.util.List;

/** REI widget shortcuts matching {@link net.satisfy.foundation.compat.RecipeViewerLayout}. */
public final class ReiWidgets {
    private ReiWidgets() {
    }

    public static void inputSlot(List<Widget> widgets, int x, int y, EntryIngredient entries) {
        widgets.add(Widgets.createSlot(new Point(x + 1, y + 1)).entries(entries).markInput());
    }

    public static void outputSlot(List<Widget> widgets, int x, int y, EntryIngredient entries) {
        widgets.add(Widgets.createSlot(new Point(x + 1, y + 1)).entries(entries).markOutput());
    }

    public static void rightArrow(List<Widget> widgets, int x, int y) {
        widgets.add(Widgets.createDrawableWidget((graphics, mouseX, mouseY, delta) -> RecipeViewerLayout.drawRightArrow(graphics, x, y)));
    }

    /** Arrow with a small grey label below, e.g. for cooking time. */
    public static void rightArrow(List<Widget> widgets, int x, int y, Component label) {
        widgets.add(Widgets.createDrawableWidget((graphics, mouseX, mouseY, delta) -> {
            RecipeViewerLayout.drawRightArrow(graphics, x, y);
            int width = Minecraft.getInstance().font.width(label);
            graphics.drawString(Minecraft.getInstance().font, label, x + (RecipeViewerLayout.ARROW_LENGTH - width) / 2, y + 19, 0x808080, false);
        }));
    }

    public static void downArrow(List<Widget> widgets, int x, int y) {
        widgets.add(Widgets.createDrawableWidget((graphics, mouseX, mouseY, delta) -> RecipeViewerLayout.drawDownArrow(graphics, x, y)));
    }
}
