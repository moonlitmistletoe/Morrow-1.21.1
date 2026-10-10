package net.moonlitmistletoe.morrow.util;

import com.mojang.logging.LogUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.moonlitmistletoe.morrow.network.HandcuffNetwork;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public final class HandcuffManager {

    private static final Logger LOGGER =
            LogUtils.getLogger();

    private static final Map<UUID, UUID> CUFFED_PLAYERS =
            new HashMap<>();

    private HandcuffManager() {
    }

    public static boolean isCuffed(
            ServerPlayer player
    ) {
        return CUFFED_PLAYERS.containsKey(
                player.getUUID()
        );
    }

    public static boolean isHolder(
            ServerPlayer target,
            ServerPlayer holder
    ) {
        UUID holderUUID =
                CUFFED_PLAYERS.get(
                        target.getUUID()
                );

        return holderUUID != null
                && holderUUID.equals(
                holder.getUUID()
        );
    }

    public static void cuff(
            ServerPlayer holder,
            ServerPlayer target
    ) {
        if (holder == target) {
            return;
        }

        if (isCuffed(holder)
                || isCuffed(target)) {
            return;
        }

        CUFFED_PLAYERS.put(
                target.getUUID(),
                holder.getUUID()
        );

        positionTarget(
                holder,
                target
        );

        HandcuffNetwork.play(
                target
        );

        LOGGER.debug(
                "Cuffed {} to {}",
                target.getGameProfile().getName(),
                holder.getGameProfile().getName()
        );
    }

    public static void uncuff(
            ServerPlayer target
    ) {
        UUID targetUUID =
                target.getUUID();

        if (!CUFFED_PLAYERS.containsKey(
                targetUUID
        )) {
            return;
        }

        CUFFED_PLAYERS.remove(
                targetUUID
        );

        HandcuffNetwork.stop(
                target
        );

        target.setDeltaMovement(
                0.0D,
                0.0D,
                0.0D
        );

        LOGGER.debug(
                "Uncuffed {}",
                target.getGameProfile().getName()
        );
    }

    @net.neoforged.bus.api.SubscribeEvent
    public static void onServerTick(
            ServerTickEvent.Post event
    ) {
        if (CUFFED_PLAYERS.isEmpty()) {
            return;
        }

        Iterator<Map.Entry<UUID, UUID>> iterator =
                CUFFED_PLAYERS.entrySet().iterator();

        while (iterator.hasNext()) {

            Map.Entry<UUID, UUID> entry =
                    iterator.next();

            ServerPlayer target =
                    event.getServer()
                            .getPlayerList()
                            .getPlayer(
                                    entry.getKey()
                            );

            ServerPlayer holder =
                    event.getServer()
                            .getPlayerList()
                            .getPlayer(
                                    entry.getValue()
                            );

            if (target == null
                    || holder == null) {

                if (target != null) {
                    HandcuffNetwork.stop(target);

                    target.setDeltaMovement(
                            0.0D,
                            0.0D,
                            0.0D
                    );
                }

                iterator.remove();
                continue;
            }

            positionTarget(
                    holder,
                    target
            );

            // Completely cancel the target's movement.
            target.setDeltaMovement(
                    0.0D,
                    0.0D,
                    0.0D
            );

            target.hurtMarked = true;
        }
    }

    private static void positionTarget(
            ServerPlayer holder,
            ServerPlayer target
    ) {
        float holderYaw =
                holder.getYRot();

        double yawRadians =
                Math.toRadians(
                        holderYaw
                );

        double forwardX =
                -Math.sin(
                        yawRadians
                );

        double forwardZ =
                Math.cos(
                        yawRadians
                );

        double distance =
                0.85D;

        double x =
                holder.getX()
                        + forwardX * distance;

        double y =
                holder.getY();

        double z =
                holder.getZ()
                        + forwardZ * distance;

        target.teleportTo(
                x,
                y,
                z
        );

        // Make the cuffed player face exactly the
        // same direction as the person holding them.
        target.setYRot(
                holderYaw
        );

        target.setXRot(
                holder.getXRot()
        );

        target.setYHeadRot(
                holderYaw
        );

        target.yBodyRot =
                holderYaw;

        target.yHeadRotO =
                holderYaw;

        target.yBodyRotO =
                holderYaw;
    }
}