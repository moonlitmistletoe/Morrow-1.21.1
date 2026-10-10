package net.moonlitmistletoe.morrow.network;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public final class HandcuffNetwork {

    private HandcuffNetwork() {
    }

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(
                HandcuffNetwork::registerPayloads
        );
    }

    private static void registerPayloads(
            RegisterPayloadHandlersEvent event
    ) {
        PayloadRegistrar registrar =
                event.registrar("1");

        registrar.playToClient(
                HandcuffAnimationPayload.TYPE,
                HandcuffAnimationPayload.STREAM_CODEC,
                (payload, context) -> {
                    context.enqueueWork(() ->
                            net.moonlitmistletoe.morrow.client.HandcuffAnimationClient
                                    .handle(payload)
                    );
                }
        );
    }

    public static void play(
            ServerPlayer target
    ) {
        PacketDistributor.sendToAllPlayers(
                new HandcuffAnimationPayload(
                        target.getUUID(),
                        true
                )
        );
    }

    public static void stop(
            ServerPlayer target
    ) {
        PacketDistributor.sendToAllPlayers(
                new HandcuffAnimationPayload(
                        target.getUUID(),
                        false
                )
        );
    }
}