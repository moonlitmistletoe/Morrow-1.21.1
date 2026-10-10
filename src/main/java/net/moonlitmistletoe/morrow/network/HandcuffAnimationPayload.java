package net.moonlitmistletoe.morrow.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.moonlitmistletoe.morrow.Morrow;

import java.util.UUID;

public record HandcuffAnimationPayload(
        UUID playerId,
        boolean play
) implements CustomPacketPayload {

    public static final Type<HandcuffAnimationPayload> TYPE =
            new Type<>(
                    ResourceLocation.fromNamespaceAndPath(
                            Morrow.MOD_ID,
                            "handcuff_animation"
                    )
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, HandcuffAnimationPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8,
                    payload -> payload.playerId().toString(),

                    ByteBufCodecs.BOOL,
                    HandcuffAnimationPayload::play,

                    (uuid, play) ->
                            new HandcuffAnimationPayload(
                                    UUID.fromString(uuid),
                                    play
                            )
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}