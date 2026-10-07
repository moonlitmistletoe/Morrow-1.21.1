package net.satisfy.foundation.text;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import dev.architectury.networking.NetworkManager;
import net.satisfy.foundation.Foundation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Client -> server packet for editing text on a block entity (signs, boards...).
 * Lines are capped at 50 chars.
 */
public record SetTextPacket(BlockPos pos, List<String> texts) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SetTextPacket> TYPE =
            new Type<>(Foundation.identifier("set_text"));

    private static final double MAX_EDIT_DISTANCE_SQR = 64.0;

    public static final StreamCodec<RegistryFriendlyByteBuf, SetTextPacket> STREAM_CODEC =
            StreamCodec.of(SetTextPacket::toNetwork, SetTextPacket::fromNetwork);

    public static void init() {
        NetworkManager.registerReceiver(NetworkManager.c2s(), TYPE, STREAM_CODEC, (packet, context) -> context.queue(() -> handle(packet, (ServerPlayer) context.getPlayer())));
    }

    public static void sendToServer(SetTextPacket packet) {
        NetworkManager.sendToServer(packet);
    }

    public static void toNetwork(RegistryFriendlyByteBuf buf, SetTextPacket msg) {
        buf.writeBlockPos(msg.pos);
        buf.writeInt(msg.texts.size());
        for (String text : msg.texts) {
            buf.writeUtf(text);
        }
    }

    public static SetTextPacket fromNetwork(RegistryFriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        int size = buf.readInt();
        List<String> texts = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            texts.add(buf.readUtf(50));
        }
        return new SetTextPacket(pos, texts);
    }

    /** Writes the lines, extra lines above {@link TextEditableBlockEntity#getTextLineCount()} get ignored. */
    public static void handle(SetTextPacket msg, ServerPlayer player) {
        Level level = player.level();
        if (level.isLoaded(msg.pos) && player.distanceToSqr(msg.pos.getCenter()) <= MAX_EDIT_DISTANCE_SQR) {
            BlockEntity entity = level.getBlockEntity(msg.pos);
            if (entity instanceof TextEditableBlockEntity editable) {
                int maxLines = editable.getTextLineCount();
                for (int i = 0; i < Math.min(msg.texts.size(), maxLines); i++) {
                    editable.setText(i, Component.literal(msg.texts.get(i)));
                }
            }
        }
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}