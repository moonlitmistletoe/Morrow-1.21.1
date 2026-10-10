package net.moonlitmistletoe.morrow.item.custom;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.moonlitmistletoe.morrow.util.HandcuffManager;

public class HandcuffsItem extends Item {

    public HandcuffsItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult interactLivingEntity(
            ItemStack stack,
            Player player,
            LivingEntity target,
            InteractionHand hand
    ) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResult.SUCCESS;
        }

        if (!(target instanceof ServerPlayer targetPlayer)) {
            return InteractionResult.PASS;
        }

        if (serverPlayer == targetPlayer) {
            return InteractionResult.PASS;
        }

        if (HandcuffManager.isCuffed(targetPlayer)) {
            if (HandcuffManager.isHolder(targetPlayer, serverPlayer)) {
                HandcuffManager.uncuff(targetPlayer);
                return InteractionResult.SUCCESS;
            }

            return InteractionResult.PASS;
        }

        if (HandcuffManager.isCuffed(serverPlayer)) {
            return InteractionResult.PASS;
        }

        HandcuffManager.cuff(serverPlayer, targetPlayer);

        return InteractionResult.SUCCESS;
    }
}