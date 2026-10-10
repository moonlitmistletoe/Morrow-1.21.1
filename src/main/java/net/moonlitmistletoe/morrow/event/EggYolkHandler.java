package net.moonlitmistletoe.morrow.event;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.ThrownEgg;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import net.moonlitmistletoe.morrow.item.ModItems;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

public class EggYolkHandler {

    @SubscribeEvent
    public static void onEggImpact(ProjectileImpactEvent event) {

        // Make sure this is a normal thrown egg
        if (!(event.getProjectile() instanceof ThrownEgg egg)) {
            return;
        }

        // Make sure the egg hit a block
        if (event.getRayTraceResult().getType() != HitResult.Type.BLOCK) {
            return;
        }

        // Get the block hit result
        BlockHitResult hitResult = (BlockHitResult) event.getRayTraceResult();

        // Only drop a yolk when hitting the top of a block (the ground)
        if (!hitResult.getDirection().getAxis().isVertical()
                || hitResult.getDirection().getAxisDirection() != net.minecraft.core.Direction.AxisDirection.POSITIVE) {
            return;
        }

        // 25% chance
        if (egg.level().random.nextFloat() >= 0.25f) {
            return;
        }

        // Create one egg yolk
        ItemStack yolk = new ItemStack(ModItems.EGG_YOLK.get());

        // Spawn the yolk where the egg landed
        ItemEntity yolkEntity = new ItemEntity(
                egg.level(),
                egg.getX(),
                egg.getY(),
                egg.getZ(),
                yolk
        );

        egg.level().addFreshEntity(yolkEntity);
    }

    @SubscribeEvent
    public static void preventEggYolkOnCampfire(PlayerInteractEvent.RightClickBlock event) {
        if (!event.getLevel().getBlockState(event.getPos()).is(Blocks.CAMPFIRE)
                && !event.getLevel().getBlockState(event.getPos()).is(Blocks.SOUL_CAMPFIRE)) {
            return;
        }

        if (!event.getItemStack().is(ModItems.EGG_YOLK.get())) {
            return;
        }

        event.setCancellationResult(InteractionResult.FAIL);
        event.setCanceled(true);
    }
}