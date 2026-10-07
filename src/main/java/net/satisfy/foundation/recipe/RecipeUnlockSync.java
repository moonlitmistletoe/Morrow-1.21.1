package net.satisfy.foundation.recipe;

import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.networking.NetworkManager;
import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.satisfy.foundation.Foundation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Mirrors the unlocked recipes of a player to the client, so the station recipe book can hide locked ones.
 * Sent on join and every time {@link RecipeUnlockManager} saves.
 */
public final class RecipeUnlockSync {
    private static Set<ResourceLocation> clientUnlocked = Set.of();

    private RecipeUnlockSync() {
    }

    public record Payload(List<ResourceLocation> unlocked) implements CustomPacketPayload {
        public static final Type<Payload> TYPE = new Type<>(Foundation.identifier("recipe_unlocks"));
        public static final StreamCodec<RegistryFriendlyByteBuf, Payload> CODEC = StreamCodec.composite(
                ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.list()), Payload::unlocked, Payload::new);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public static void init() {
        if (Platform.getEnvironment() == Env.CLIENT) {
            NetworkManager.registerReceiver(NetworkManager.s2c(), Payload.TYPE, Payload.CODEC,
                    (payload, context) -> context.queue(() -> clientUnlocked = new HashSet<>(payload.unlocked())));
        } else {
            NetworkManager.registerS2CPayloadType(Payload.TYPE, Payload.CODEC);
        }
        PlayerEvent.PLAYER_JOIN.register(player -> send(player, RecipeUnlockManager.loadUnlockedRecipes(player)));
    }

    public static void send(ServerPlayer player, Set<ResourceLocation> unlocked) {
        NetworkManager.sendToPlayer(player, new Payload(List.copyOf(unlocked)));
    }

    /** Client side copy, empty until the first sync arrived. */
    public static Set<ResourceLocation> clientUnlocked() {
        return clientUnlocked;
    }
}
