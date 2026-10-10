package net.moonlitmistletoe.morrow.event;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import net.minecraft.server.level.ServerLevel;

import java.util.Set;

public class CropHarvestHandler {

    private static final Set<String> EXTRA_HARVEST_TOOLS = Set.of(
            "advancednetherite:netherite_diamond_hoe",
            "advancednetherite:netherite_emerald_hoe",
            "advancednetherite:netherite_gold_hoe",
            "advancednetherite:netherite_iron_hoe",
            "advancednetherite:super_diamond_hoe",
            "advancednetherite:super_gold_hoe",
            "advancednetherite:super_iron_hoe",
            "advancednetherite:super_netherite_hoe",
            "born_in_chaos_v1:nightmare_scythe"
    );

    @SubscribeEvent
    public static void onRightClickCrop(PlayerInteractEvent.RightClickBlock event) {

        Level level = event.getLevel();

        // Only run on the server
        if (level.isClientSide()) {
            return;
        }

        ItemStack heldItem = event.getItemStack();

        // Must be holding a vanilla/modded HoeItem
        // OR one of the specifically registered extra tools
        if (!isHarvestTool(heldItem)) {
            return;
        }

        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);

        // Must be a crop
        if (!(state.getBlock() instanceof CropBlock crop)) {
            return;
        }

        // Must be fully grown
        if (!crop.isMaxAge(state)) {
            return;
        }

        ServerLevel serverLevel = (ServerLevel) level;

        // Get the crop's normal drops
        var drops = Block.getDrops(
                state,
                serverLevel,
                pos,
                null,
                event.getEntity(),
                heldItem
        );

        // Drop the harvested items
        for (ItemStack drop : drops) {
            Block.popResource(level, pos, drop);
        }

        // Reset the crop to stage 0
        level.setBlock(
                pos,
                crop.getStateForAge(0),
                Block.UPDATE_ALL
        );

        // Prevent the normal right-click action
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
    }

    private static boolean isHarvestTool(ItemStack stack) {

        // All normal vanilla hoes and modded hoes extending HoeItem
        if (stack.getItem() instanceof HoeItem) {
            return true;
        }

        // Specifically registered additional tools
        return EXTRA_HARVEST_TOOLS.contains(
                BuiltInRegistries.ITEM.getKey(stack.getItem()).toString()
        );
    }
}