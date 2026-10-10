package net.moonlitmistletoe.morrow.client;

import dev.kosmx.playerAnim.api.IPlayable;
import dev.kosmx.playerAnim.api.layered.AnimationStack;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationAccess;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import net.moonlitmistletoe.morrow.Morrow;
import net.moonlitmistletoe.morrow.network.HandcuffAnimationPayload;

public final class HandcuffAnimationClient {

    private static final int ANIMATION_PRIORITY = 100;

    private static final ResourceLocation ANIMATION_ID =
            ResourceLocation.fromNamespaceAndPath(
                    Morrow.MOD_ID,
                    "spe_hands_behind"
            );

    private HandcuffAnimationClient() {
    }

    public static void handle(
            HandcuffAnimationPayload payload
    ) {
        Minecraft minecraft =
                Minecraft.getInstance();

        if (minecraft.level == null) {
            return;
        }

        if (!(minecraft.level.getPlayerByUUID(
                payload.playerId()
        ) instanceof AbstractClientPlayer player)) {
            return;
        }

        if (payload.play()) {
            play(player);
        } else {
            stop(player);
        }
    }

    private static void play(
            AbstractClientPlayer player
    ) {
        AnimationStack animationStack =
                PlayerAnimationAccess.getPlayerAnimLayer(
                        player
                );

        IPlayable playable =
                PlayerAnimationRegistry.getAnimation(
                        ANIMATION_ID
                );

        if (playable == null) {
            Morrow.LOGGER.error(
                    "Could not find handcuff animation: {}",
                    ANIMATION_ID
            );
            return;
        }

        IAnimation animation =
                playable.playAnimation();

        ModifierLayer<IAnimation> animationLayer =
                new ModifierLayer<>();

        animationLayer.setAnimation(
                animation
        );

        animationStack.removeLayer(
                ANIMATION_PRIORITY
        );

        animationStack.addAnimLayer(
                ANIMATION_PRIORITY,
                animationLayer
        );

        Morrow.LOGGER.debug(
                "Started handcuff animation on {}",
                player.getGameProfile().getName()
        );
    }

    private static void stop(
            AbstractClientPlayer player
    ) {
        AnimationStack animationStack =
                PlayerAnimationAccess.getPlayerAnimLayer(
                        player
                );

        animationStack.removeLayer(
                ANIMATION_PRIORITY
        );

        Morrow.LOGGER.debug(
                "Stopped handcuff animation on {}",
                player.getGameProfile().getName()
        );
    }
}