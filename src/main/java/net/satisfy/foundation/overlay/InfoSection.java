package net.satisfy.foundation.overlay;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * One block of content in the {@link BlockInfoOverlay}: title plus lines,
 * item rows, an icon grid or an image. Use the static factories, then the
 * {@code with...} methods to add stuff.
 */
public record InfoSection(Component title, List<Component> lines, List<ItemStack> icons, int columns, List<Row> rows, @Nullable ResourceLocation image, int imageSize, @Nullable ResourceLocation placeholder, boolean decorations) {
    public static final int ROW_COLUMNS = 6;
    public static final int GRID_COLUMNS = 3;
    private static final int ICON_SIZE = 16;
    private static final int ICON_STEP = 19;
    private static final int SPRITE_SIZE = 12;
    private static final int TITLE_HEIGHT = 13;
    private static final int TEXT_HEIGHT = 8;
    private static final int LINE_HEIGHT = 10;
    private static final int COLOR_TITLE = 0xFFE8C97A;
    private static final int COLOR_TEXT = 0xFFFFFFFF;
    private static final int COLOR_SLOT = 0x28000000;

    /** Icon (item or sprite) with some text next to it. */
    public record Row(ItemStack icon, Component text, @Nullable ResourceLocation sprite) {
        public static Row item(ItemStack icon, Component text) {
            return new Row(icon, text, null);
        }

        public static Row sprite(ResourceLocation sprite, Component text) {
            return new Row(ItemStack.EMPTY, text, sprite);
        }
    }

    /** Just a title, nothing else. */
    public static InfoSection title(Component title) {
        return new InfoSection(title, List.of(), List.of(), ROW_COLUMNS, List.of(), null, 0, null, false);
    }

    /** Grid of item icons, e.g. an inventory. */
    public static InfoSection icons(Component title, List<ItemStack> icons, int columns) {
        return new InfoSection(title, List.of(), icons, columns, List.of(), null, 0, null, false);
    }

    /** Title with a square image below. */
    public static InfoSection image(Component title, ResourceLocation image, int size) {
        return new InfoSection(title, List.of(), List.of(), ROW_COLUMNS, List.of(), image, size, null, false);
    }

    public static InfoSection lines(Component title, List<Component> lines) {
        return new InfoSection(title, lines, List.of(), ROW_COLUMNS, List.of(), null, 0, null, false);
    }

    public static InfoSection rows(Component title, List<Row> rows) {
        return new InfoSection(title, List.of(), List.of(), ROW_COLUMNS, rows, null, 0, null, false);
    }

    public InfoSection withLines(List<Component> lines) {
        return new InfoSection(this.title, lines, this.icons, this.columns, this.rows, this.image, this.imageSize, this.placeholder, this.decorations);
    }

    public InfoSection withRows(List<Row> rows) {
        return new InfoSection(this.title, this.lines, this.icons, this.columns, rows, this.image, this.imageSize, this.placeholder, this.decorations);
    }

    public InfoSection withPlaceholder(ResourceLocation placeholder) {
        return new InfoSection(this.title, this.lines, this.icons, this.columns, this.rows, this.image, this.imageSize, placeholder, this.decorations);
    }

    public InfoSection withDecorations() {
        return new InfoSection(this.title, this.lines, this.icons, this.columns, this.rows, this.image, this.imageSize, this.placeholder, true);
    }

    int iconRows() {
        return (this.icons.size() + this.columns - 1) / this.columns;
    }

    private int rowWidth(Font font) {
        return this.rows.stream().mapToInt(row -> ICON_STEP + 2 + font.width(row.text())).max().orElse(0);
    }

    int width(Font font) {
        int iconWidth = Math.min(this.icons.size(), this.columns) * ICON_STEP - (this.icons.isEmpty() ? 0 : ICON_STEP - ICON_SIZE - 2);
        int lineWidth = this.lines.stream().mapToInt(font::width).max().orElse(0);
        return Math.max(Math.max(font.width(this.title), iconWidth), Math.max(Math.max(lineWidth, this.rowWidth(font)), this.imageSize));
    }

    int height() {
        if (this.icons.isEmpty() && this.image == null && this.lines.isEmpty() && this.rows.isEmpty()) {
            return TEXT_HEIGHT;
        }
        return TITLE_HEIGHT + this.rows.size() * ICON_STEP + this.lines.size() * LINE_HEIGHT + this.iconRows() * ICON_STEP + (this.image != null ? this.imageSize + 2 : 0);
    }

    void draw(GuiGraphics graphics, Font font, int y, int width) {
        graphics.drawString(font, this.title, (width - font.width(this.title)) / 2, y, COLOR_TITLE);
        int cursor = y + TITLE_HEIGHT;
        int entryX = (width - this.rowWidth(font)) / 2;
        for (Row row : this.rows) {
            if (row.sprite() != null) {
                int offset = (ICON_SIZE - SPRITE_SIZE) / 2;
                graphics.blit(row.sprite(), entryX + offset, cursor + offset, 0, 0, SPRITE_SIZE, SPRITE_SIZE, SPRITE_SIZE, SPRITE_SIZE);
            } else {
                graphics.fill(entryX - 1, cursor - 1, entryX + ICON_SIZE + 1, cursor + ICON_SIZE + 1, COLOR_SLOT);
                graphics.renderItem(row.icon(), entryX, cursor);
            }
            graphics.drawString(font, row.text(), entryX + ICON_STEP + 2, cursor + (ICON_SIZE - font.lineHeight) / 2 + 1, COLOR_TEXT);
            cursor += ICON_STEP;
        }
        for (Component line : this.lines) {
            graphics.drawString(font, line, (width - font.width(line)) / 2, cursor, COLOR_TEXT);
            cursor += LINE_HEIGHT;
        }
        for (int row = 0; row < this.iconRows(); row++) {
            int start = row * this.columns;
            int inRow = Math.min(this.columns, this.icons.size() - start);
            int rowX = (width - (inRow * ICON_STEP - (ICON_STEP - ICON_SIZE))) / 2;
            for (int i = 0; i < inRow; i++) {
                int iconX = rowX + i * ICON_STEP;
                int iconY = cursor + row * ICON_STEP;
                ItemStack stack = this.icons.get(start + i);
                graphics.fill(iconX - 1, iconY - 1, iconX + ICON_SIZE + 1, iconY + ICON_SIZE + 1, COLOR_SLOT);
                if (stack.isEmpty() && this.placeholder != null) {
                    graphics.blit(this.placeholder, iconX, iconY, 0, 0, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
                } else {
                    graphics.renderItem(stack, iconX, iconY);
                    if (this.decorations) {
                        graphics.renderItemDecorations(font, stack, iconX, iconY);
                    }
                }
            }
        }
        cursor += this.iconRows() * ICON_STEP;
        if (this.image != null) {
            graphics.blit(this.image, (width - this.imageSize) / 2, cursor, 0, 0, this.imageSize, this.imageSize, this.imageSize, this.imageSize);
        }
    }
}
