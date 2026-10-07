package net.satisfy.foundation.compat;

import com.mojang.math.Axis;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * Shared layout math for recipe viewer categories (REI/JEI/EMI),
 * so all of them look the same. All values in gui pixels.
 */
public final class RecipeViewerLayout {
    public static final int SLOT = 18;
    public static final int ARROW_LENGTH = 24;
    public static final int ARROW_THICKNESS = 16;
    public static final int GRID = 3 * SLOT;
    public static final int ARROW_GAP = 3;
    public static final int ORDERED_STEP = SLOT + ARROW_GAP + ARROW_LENGTH + ARROW_GAP;
    public static final int ASSEMBLY_HEIGHT = GRID;
    public static final int ROW_WIDTH = 130;
    public static final int ROW_HEIGHT = 36;

    private static final ResourceLocation ARROW = ResourceLocation.withDefaultNamespace("container/furnace/burn_progress");

    private RecipeViewerLayout() {
    }

    /** Width needed for an ordered assembly with this many inputs. */
    public static int assemblyWidth(int maxOrderedInputs) {
        return maxOrderedInputs * ORDERED_STEP + SLOT;
    }

    /** Draws the vanilla furnace arrow pointing right. */
    public static void drawRightArrow(GuiGraphics graphics, int x, int y) {
        graphics.blitSprite(ARROW, x, y, ARROW_LENGTH, ARROW_THICKNESS);
    }

    /** Same arrow, just rotated down. */
    public static void drawDownArrow(GuiGraphics graphics, int x, int y) {
        graphics.pose().pushPose();
        graphics.pose().translate(x + ARROW_THICKNESS, y, 0.0F);
        graphics.pose().mulPose(Axis.ZP.rotationDegrees(90.0F));
        drawRightArrow(graphics, 0, 0);
        graphics.pose().popPose();
    }

    /**
     * Calculates slot and arrow positions for an assembly recipe.
     *
     * @param ordered true = inputs in a row with arrows between, false = 3x3 grid
     * @param maxOrderedInputs used for the total width so all pages line up
     */
    public static AssemblyLayout assembly(int inputs, boolean ordered, int maxOrderedInputs) {
        int width = assemblyWidth(maxOrderedInputs);
        List<Pos> slots = new ArrayList<>();
        List<Pos> arrows = new ArrayList<>();
        int arrowY = (GRID - ARROW_THICKNESS) / 2;
        int rowY = (GRID - SLOT) / 2;
        if (ordered) {
            int x = (width - (inputs * ORDERED_STEP + SLOT)) / 2;
            for (int i = 0; i < inputs; i++) {
                slots.add(new Pos(x, rowY));
                arrows.add(new Pos(x + SLOT + ARROW_GAP, arrowY + 1));
                x += ORDERED_STEP;
            }
            return new AssemblyLayout(slots, arrows, new Pos(x, rowY));
        }
        int gridWidth = GRID + ARROW_GAP * 2 + ARROW_LENGTH + ARROW_GAP * 2 + SLOT;
        int x = (width - gridWidth) / 2;
        for (int i = 0; i < 9; i++) {
            slots.add(new Pos(x + (i % 3) * SLOT, (i / 3) * SLOT));
        }
        int arrowX = x + GRID + ARROW_GAP * 2;
        arrows.add(new Pos(arrowX, arrowY + 1));
        return new AssemblyLayout(slots, arrows, new Pos(arrowX + ARROW_LENGTH + ARROW_GAP * 2, rowY));
    }

    public record Pos(int x, int y) {
    }

    public record AssemblyLayout(List<Pos> slots, List<Pos> arrows, Pos output) {
    }
}
