package net.satisfy.foundation.block;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.DyeColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum ClothColor implements StringRepresentable {
    NONE(null),
    WHITE(DyeColor.WHITE),
    ORANGE(DyeColor.ORANGE),
    MAGENTA(DyeColor.MAGENTA),
    LIGHT_BLUE(DyeColor.LIGHT_BLUE),
    YELLOW(DyeColor.YELLOW),
    LIME(DyeColor.LIME),
    PINK(DyeColor.PINK),
    GRAY(DyeColor.GRAY),
    LIGHT_GRAY(DyeColor.LIGHT_GRAY),
    CYAN(DyeColor.CYAN),
    PURPLE(DyeColor.PURPLE),
    BLUE(DyeColor.BLUE),
    BROWN(DyeColor.BROWN),
    GREEN(DyeColor.GREEN),
    RED(DyeColor.RED),
    BLACK(DyeColor.BLACK);

    @Nullable
    private final DyeColor color;

    ClothColor(@Nullable DyeColor color) {
        this.color = color;
    }

    @Nullable
    public DyeColor color() {
        return color;
    }

    public int tint() {
        return color == null ? 0xFFFFFFFF : color.getTextureDiffuseColor();
    }

    public static ClothColor of(DyeColor color) {
        for (ClothColor cloth : values()) {
            if (cloth.color == color) return cloth;
        }
        return NONE;
    }

    @Override
    public @NotNull String getSerializedName() {
        return color == null ? "none" : color.getSerializedName();
    }
}
