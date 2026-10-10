package net.moonlitmistletoe.morrow.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.moonlitmistletoe.morrow.network.HandcuffNetwork;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

public final class MorrowCommands {

    private MorrowCommands() {
    }

    public static void register(
            RegisterCommandsEvent event
    ) {
        CommandDispatcher<CommandSourceStack> dispatcher =
                event.getDispatcher();

        dispatcher.register(
                Commands.literal("morrow")
                        .then(
                                Commands.literal("testcuffs")
                                        .executes(context ->
                                                testCuffs(
                                                        context.getSource()
                                                )
                                        )
                        )
                        .then(
                                Commands.literal("testuncuff")
                                        .executes(context ->
                                                testUncuff(
                                                        context.getSource()
                                                )
                                        )
                        )
        );
    }

    private static int testCuffs(
            CommandSourceStack source
    ) throws CommandSyntaxException {

        ServerPlayer player =
                source.getPlayerOrException();

        HandcuffNetwork.play(player);

        player.addEffect(
                new MobEffectInstance(
                        MobEffects.MOVEMENT_SLOWDOWN,
                        40,
                        255,
                        false,
                        false,
                        false
                )
        );

        source.sendSuccess(
                () -> Component.literal(
                        "Handcuff animation test started."
                ),
                false
        );

        return 1;
    }

    private static int testUncuff(
            CommandSourceStack source
    ) throws CommandSyntaxException {

        ServerPlayer player =
                source.getPlayerOrException();

        HandcuffNetwork.stop(player);

        player.removeEffect(
                MobEffects.MOVEMENT_SLOWDOWN
        );

        source.sendSuccess(
                () -> Component.literal(
                        "Handcuff animation test stopped."
                ),
                false
        );

        return 1;
    }
}