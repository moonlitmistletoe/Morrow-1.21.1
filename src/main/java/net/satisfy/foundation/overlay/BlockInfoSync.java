package net.satisfy.foundation.overlay;

import dev.architectury.networking.NetworkManager;
import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.satisfy.foundation.Foundation;

import java.util.ArrayList;
import java.util.List;

/**
 * Pulls server side data for the overlay. Client asks for a pos,
 * all registered {@link Handler}s write into one tag and it gets sent back.
 * Only one pos is cached on the client, which is enough for the overlay.
 */
public final class BlockInfoSync {
    private static final double MAX_DISTANCE_SQR = 64.0;
    private static final int REQUEST_INTERVAL = 10;
    private static final CompoundTag EMPTY = new CompoundTag();
    private static final List<Handler> HANDLERS = new ArrayList<>();

    private static CompoundTag clientData = EMPTY;
    private static BlockPos clientPos = BlockPos.ZERO;
    private static BlockPos lastRequest;

    private BlockInfoSync() {
    }

    /** Writes server data for a pos into the tag. Keep keys unique per mod. */
    @FunctionalInterface
    public interface Handler {
        void write(ServerLevel level, BlockPos pos, ServerPlayer player, CompoundTag tag);
    }

    public record Request(BlockPos pos) implements CustomPacketPayload {
        public static final Type<Request> TYPE = new Type<>(Foundation.identifier("block_info_request"));
        public static final StreamCodec<RegistryFriendlyByteBuf, Request> CODEC = StreamCodec.composite(BlockPos.STREAM_CODEC, Request::pos, Request::new);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record Response(BlockPos pos, CompoundTag data) implements CustomPacketPayload {
        public static final Type<Response> TYPE = new Type<>(Foundation.identifier("block_info"));
        public static final StreamCodec<RegistryFriendlyByteBuf, Response> CODEC = StreamCodec.composite(BlockPos.STREAM_CODEC, Response::pos, ByteBufCodecs.COMPOUND_TAG, Response::data, Response::new);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    /** Register on both sides (common init), handlers only run on the server. */
    public static void registerHandler(Handler handler) {
        HANDLERS.add(handler);
    }

    public static void init() {
        NetworkManager.registerReceiver(NetworkManager.c2s(), Request.TYPE, Request.CODEC, (payload, context) -> context.queue(() -> {
            if (context.getPlayer() instanceof ServerPlayer player && player.blockPosition().distSqr(payload.pos()) <= MAX_DISTANCE_SQR) {
                CompoundTag tag = new CompoundTag();
                for (Handler handler : HANDLERS) {
                    handler.write(player.serverLevel(), payload.pos(), player, tag);
                }
                NetworkManager.sendToPlayer(player, new Response(payload.pos(), tag));
            }
        }));
        if (Platform.getEnvironment() == Env.CLIENT) {
            NetworkManager.registerReceiver(NetworkManager.s2c(), Response.TYPE, Response.CODEC, (payload, context) -> context.queue(() -> {
                clientPos = payload.pos();
                clientData = payload.data();
            }));
        } else {
            NetworkManager.registerS2CPayloadType(Response.TYPE, Response.CODEC);
        }
    }

    /**
     * Client side: returns the last data for this pos and re-requests every 10 ticks.
     * Returns an empty tag until the first answer arrived.
     */
    public static CompoundTag poll(Level level, BlockPos pos) {
        if (level.getGameTime() % REQUEST_INTERVAL == 0 || !pos.equals(lastRequest)) {
            lastRequest = pos.immutable();
            NetworkManager.sendToServer(new Request(pos));
        }
        return pos.equals(clientPos) ? clientData : EMPTY;
    }
}
