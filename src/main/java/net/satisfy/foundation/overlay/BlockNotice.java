package net.satisfy.foundation.overlay;

import java.util.List;
import net.minecraft.network.codec.ByteBufCodecs;
import dev.architectury.networking.NetworkManager;
import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.satisfy.foundation.Foundation;
import org.jetbrains.annotations.NotNull;

public final class BlockNotice {
    private BlockNotice() {
    }

    public record Packet(BlockPos pos, Component message, List<Component> lines) implements CustomPacketPayload {
        public static final Type<Packet> TYPE = new Type<>(Foundation.identifier("block_notice"));
        public static final StreamCodec<RegistryFriendlyByteBuf, Packet> CODEC = StreamCodec.composite(
                BlockPos.STREAM_CODEC, Packet::pos,
                ComponentSerialization.STREAM_CODEC, Packet::message,
                ComponentSerialization.STREAM_CODEC.apply(ByteBufCodecs.list()), Packet::lines,
                Packet::new);

        @Override
        public @NotNull Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public static void init() {
        if (Platform.getEnvironment() == Env.CLIENT) {
            NetworkManager.registerReceiver(NetworkManager.s2c(), Packet.TYPE, Packet.CODEC, (packet, context) -> context.queue(() -> BlockInfoOverlay.showNotice(packet.pos(), packet.message(), packet.lines())));
        } else {
            NetworkManager.registerS2CPayloadType(Packet.TYPE, Packet.CODEC);
        }
    }

    public static void send(ServerPlayer player, BlockPos pos, Component message) {
        send(player, pos, message, List.of());
    }

    public static void send(ServerPlayer player, BlockPos pos, Component title, List<Component> lines) {
        NetworkManager.sendToPlayer(player, new Packet(pos, title, lines));
    }
}
