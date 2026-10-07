package net.satisfy.foundation.overlay;

import java.util.Map;
import java.util.HashMap;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.architectury.event.events.client.ClientGuiEvent;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Little info panel floating next to blocks in the world (think Jade, but simple).
 * Mods register {@link BlockInfoProvider}s, the first one returning something wins.
 * Tries to render above the block, falls back to the side if there is no room.
 */
public final class BlockInfoOverlay {
    private static final int SEPARATOR_HEIGHT = 9;
    private static final int MIN_WIDTH = 56;
    private static final int COLOR_SEPARATOR = 0x40FFFFFF;
    private static final int PANEL_GAP = 6;
    private static final int SCREEN_MARGIN = 4;
    private static final double ABOVE_OFFSET = 0.45;
    private static final double SIDE_OFFSET = 0.7;
    private static final float[] ABOVE_SCALES = {1.0F, 0.8F, 0.65F};
    private static final float SIDE_SCALE = 0.8F;
    private static final long NOTICE_DURATION = 2000L;
    private static final long NOTICE_FADE = 600L;
    private static final int NOTICE_BACKGROUND = 0xF02A1C0C;
    private static final int NOTICE_BORDER_TOP = 0xF0F2C94C;
    private static final int NOTICE_BORDER_BOTTOM = 0xF0A0641E;
    private static final int TOOLTIP_BACKGROUND = 0xF0100010;
    private static final int TOOLTIP_BORDER_TOP = 0x505000FF;
    private static final int TOOLTIP_BORDER_BOTTOM = 0x5028007F;

    private static final List<BlockInfoProvider> PROVIDERS = new ArrayList<>();
    private static final Map<BlockPos, PanelState> PANEL_STATES = new HashMap<>();
    private static final float STICKY_MARGIN = 24.0F;
    private static final float SMOOTHING_MILLIS = 60.0F;
    private static final long STATE_TIMEOUT = 500L;
    private static final int MAX_PANEL_STATES = 32;

    private record PanelState(float x, float y, float scale, float targetScale, boolean above, long lastSeen) {
    }
    private static final List<Supplier<Collection<BlockPos>>> TRACKERS = new ArrayList<>();
    private static boolean initialized;
    private static BlockPos noticePos;
    private static Component noticeMessage;
    private static List<Component> noticeLines = List.of();
    private static long noticeStart;

    private BlockInfoOverlay() {
    }

    /** Registers the hud render hook, safe to call multiple times. */
    public static void init() {
        if (!initialized) {
            initialized = true;
            ClientGuiEvent.RENDER_HUD.register(BlockInfoOverlay::render);
        }
    }

    /** Adds a provider. Order matters, first match wins. */
    public static void registerProvider(BlockInfoProvider provider) {
        PROVIDERS.add(provider);
    }

    /** Shows a short golden notice at a block for ~2 seconds, fades out at the end. */
    public static void showNotice(BlockPos pos, Component message) {
        showNotice(pos, message, List.of());
    }

    public static void showNotice(BlockPos pos, Component title, List<Component> lines) {
        noticePos = pos.immutable();
        noticeMessage = title;
        noticeLines = List.copyOf(lines);
        noticeStart = System.currentTimeMillis();
    }

    /** Positions returned here always get a panel, even when not looked at. */
    public static void registerTracker(Supplier<Collection<BlockPos>> tracker) {
        TRACKERS.add(tracker);
    }

    private static void render(GuiGraphics graphics, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null || minecraft.options.hideGui) {
            return;
        }
        Level level = minecraft.level;
        graphics.flush();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        BlockPos notice = drawNotice(graphics, minecraft);
        BlockPos targeted = null;
        if (minecraft.hitResult instanceof BlockHitResult hit && hit.getType() == HitResult.Type.BLOCK) {
            targeted = hit.getBlockPos();
            if (!targeted.equals(notice)) {
                drawFirst(graphics, minecraft, level, targeted, hit);
            }
        }
        for (Supplier<Collection<BlockPos>> tracker : TRACKERS) {
            for (BlockPos pos : tracker.get()) {
                if (!pos.equals(targeted) && !pos.equals(notice)) {
                    drawFirst(graphics, minecraft, level, pos, null);
                }
            }
        }
    }

    private static BlockPos drawNotice(GuiGraphics graphics, Minecraft minecraft) {
        if (noticePos == null) {
            return null;
        }
        long elapsed = System.currentTimeMillis() - noticeStart;
        if (elapsed >= NOTICE_DURATION) {
            noticePos = null;
            noticeMessage = null;
            return null;
        }
        float alpha = Mth.clamp((NOTICE_DURATION - elapsed) / (float) NOTICE_FADE, 0.0F, 1.0F);
        RenderSystem.enableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);
        drawPanel(graphics, minecraft, noticePos, minecraft.level.getBlockState(noticePos), null, List.of(noticeLines.isEmpty() ? InfoSection.title(noticeMessage) : InfoSection.lines(noticeMessage, noticeLines)), false, true);
        graphics.flush();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        return noticePos;
    }

    private static void drawFirst(GuiGraphics graphics, Minecraft minecraft, Level level, BlockPos pos, BlockHitResult hit) {
        BlockState state = level.getBlockState(pos);
        for (BlockInfoProvider provider : PROVIDERS) {
            List<InfoSection> sections = provider.describe(level, pos, state, hit);
            if (!sections.isEmpty()) {
                drawPanel(graphics, minecraft, pos, state, provider, sections, true, false);
                return;
            }
        }
    }

    private static void drawPanel(GuiGraphics graphics, Minecraft minecraft, BlockPos pos, BlockState state, BlockInfoProvider provider, List<InfoSection> sections, boolean clamp, boolean notice) {
        VoxelShape shape = state.getShape(minecraft.level, pos);
        double top = shape.isEmpty() ? 1.0 : shape.max(Direction.Axis.Y);
        Vec3 center = Vec3.atBottomCenterOf(pos).add(0.0, top / 2.0, 0.0);
        Optional<float[]> above = project(minecraft, graphics, Vec3.atBottomCenterOf(pos).add(0.0, top + ABOVE_OFFSET, 0.0));
        Optional<float[]> middle = project(minecraft, graphics, center);
        if (above.isEmpty() || middle.isEmpty()) {
            return;
        }
        Font font = minecraft.font;
        int width = Math.max(MIN_WIDTH, sections.stream().mapToInt(section -> section.width(font)).max().orElse(0));
        int height = 0;
        for (int i = 0; i < sections.size(); i++) {
            height += sections.get(i).height() + (i > 0 ? SEPARATOR_HEIGHT : 0);
        }
        PanelState previous = PANEL_STATES.get(pos);
        long now = System.currentTimeMillis();
        boolean fresh = previous == null || now - previous.lastSeen > STATE_TIMEOUT;
        float scale = SIDE_SCALE;
        float x;
        float y = -1.0F;
        boolean placedAbove = false;
        for (float candidate : ABOVE_SCALES) {
            float panelTop = above.get()[1] - height * candidate - PANEL_GAP;
            boolean sticky = !fresh && previous.above && previous.targetScale == candidate;
            if (panelTop >= SCREEN_MARGIN - (sticky ? STICKY_MARGIN : 0.0F)) {
                scale = candidate;
                y = panelTop;
                placedAbove = true;
                break;
            }
        }
        if (placedAbove && !fresh && !previous.above) {
            float panelTop = above.get()[1] - height * scale - PANEL_GAP;
            if (panelTop < SCREEN_MARGIN + STICKY_MARGIN) {
                placedAbove = false;
            }
        }
        if (placedAbove) {
            x = above.get()[0] - width * scale / 2.0F;
        } else {
            scale = SIDE_SCALE;
            Optional<float[]> edge = project(minecraft, graphics, center.add(new Vec3(minecraft.gameRenderer.getMainCamera().getLeftVector()).scale(-SIDE_OFFSET)));
            x = (edge.isPresent() ? edge.get()[0] : middle.get()[0] + width * scale) + PANEL_GAP;
            y = middle.get()[1] - height * scale / 2.0F;
        }
        float targetScale = scale;
        if (!fresh) {
            float blend = 1.0F - (float) Math.exp(-(now - previous.lastSeen) / SMOOTHING_MILLIS);
            x = Mth.lerp(blend, previous.x, x);
            y = Mth.lerp(blend, previous.y, y);
            scale = Mth.lerp(blend, previous.scale, scale);
        }
        PANEL_STATES.put(pos.immutable(), new PanelState(x, y, scale, targetScale, placedAbove, now));
        if (PANEL_STATES.size() > MAX_PANEL_STATES) {
            PANEL_STATES.entrySet().removeIf(entry -> now - entry.getValue().lastSeen > STATE_TIMEOUT);
        }
        if (clamp) {
            x = Math.clamp(x, SCREEN_MARGIN, graphics.guiWidth() - width * scale - SCREEN_MARGIN);
            y = Math.clamp(y, SCREEN_MARGIN, graphics.guiHeight() - height * scale - SCREEN_MARGIN);
        }
        graphics.flush();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        graphics.pose().pushPose();
        graphics.pose().translate(x, y, 400.0F);
        graphics.pose().scale(scale, scale, 1.0F);
        if (provider != null) {
            provider.beforeBackground(minecraft.level, pos, state);
        }
        RenderSystem.disableDepthTest();
        if (notice) {
            drawBackground(graphics, width, height, NOTICE_BACKGROUND, NOTICE_BORDER_TOP, NOTICE_BORDER_BOTTOM);
        } else {
            drawBackground(graphics, width, height, TOOLTIP_BACKGROUND, TOOLTIP_BORDER_TOP, TOOLTIP_BORDER_BOTTOM);
        }
        graphics.flush();
        RenderSystem.enableDepthTest();
        int cursor = 0;
        for (int i = 0; i < sections.size(); i++) {
            if (i > 0) {
                graphics.fill(4, cursor + SEPARATOR_HEIGHT / 2, width - 4, cursor + SEPARATOR_HEIGHT / 2 + 1, COLOR_SEPARATOR);
                cursor += SEPARATOR_HEIGHT;
            }
            sections.get(i).draw(graphics, font, cursor, width);
            cursor += sections.get(i).height();
        }
        graphics.pose().popPose();
        graphics.flush();
    }

    private static void drawBackground(GuiGraphics graphics, int width, int height, int background, int borderTop, int borderBottom) {
        graphics.fill(-3, -4, width + 3, -3, background);
        graphics.fill(-3, height + 3, width + 3, height + 4, background);
        graphics.fill(-3, -3, width + 3, height + 3, background);
        graphics.fill(-4, -3, -3, height + 3, background);
        graphics.fill(width + 3, -3, width + 4, height + 3, background);
        graphics.fillGradient(-3, -2, -2, height + 2, borderTop, borderBottom);
        graphics.fillGradient(width + 2, -2, width + 3, height + 2, borderTop, borderBottom);
        graphics.fill(-3, -3, width + 3, -2, borderTop);
        graphics.fill(-3, height + 2, width + 3, height + 3, borderBottom);
    }

    private static Optional<float[]> project(Minecraft minecraft, GuiGraphics graphics, Vec3 point) {
        Camera camera = minecraft.gameRenderer.getMainCamera();
        if (!camera.isInitialized() || minecraft.player == null) {
            return Optional.empty();
        }
        Vec3 offset = point.subtract(camera.getPosition());
        Vector3f look = camera.getLookVector();
        Vector3f up = camera.getUpVector();
        Vector3f left = camera.getLeftVector();
        double depth = offset.x * look.x() + offset.y * look.y() + offset.z * look.z();
        if (depth <= 0.05) {
            return Optional.empty();
        }
        double side = (offset.x * left.x() + offset.y * left.y() + offset.z * left.z()) / depth;
        double vertical = (offset.x * up.x() + offset.y * up.y() + offset.z * up.z()) / depth;
        double fov = minecraft.options.fov().get() * minecraft.player.getFieldOfViewModifier();
        double scale = (graphics.guiHeight() / 2.0) / Math.tan(Math.toRadians(fov) / 2.0);
        return Optional.of(new float[]{(float) (graphics.guiWidth() / 2.0 - side * scale), (float) (graphics.guiHeight() / 2.0 - vertical * scale)});
    }
}
