package net.moonlitmistletoe.morrow.item.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.moonlitmistletoe.morrow.block.JamBlock;

import java.util.function.Supplier;

public class JamItem extends Item {

    private final Supplier<? extends Block> jamBlock;

    public JamItem(
            Properties properties,
            Supplier<? extends Block> jamBlock
    ) {
        super(properties);
        this.jamBlock = jamBlock;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public SoundEvent getDrinkingSound() {
        return SoundEvents.GENERIC_DRINK;
    }

    @Override
    public SoundEvent getEatingSound() {
        return SoundEvents.GENERIC_DRINK;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {

        /*
         * Normal right-click remains available for drinking.
         * Shift + right-click places the jam jar.
         */
        if (context.getPlayer() == null
                || !context.getPlayer().isShiftKeyDown()) {
            return super.useOn(context);
        }

        Level level = context.getLevel();
        Block block = jamBlock.get();

        BlockPos clickedPos =
                context.getClickedPos();

        BlockState clickedState =
                level.getBlockState(clickedPos);

        /*
         * If clicking an existing stack of this jam,
         * add another jar to it.
         */
        if (clickedState.getBlock() == block
                && clickedState.hasProperty(JamBlock.STACK)) {

            int stack =
                    clickedState.getValue(JamBlock.STACK);

            if (stack < 8) {

                if (!level.isClientSide) {

                    level.setBlock(
                            clickedPos,
                            clickedState.setValue(
                                    JamBlock.STACK,
                                    stack + 1
                            ),
                            3
                    );

                    if (!context.getPlayer()
                            .getAbilities()
                            .instabuild) {

                        context.getItemInHand()
                                .shrink(1);
                    }
                }

                return InteractionResult.sidedSuccess(
                        level.isClientSide
                );
            }

            return InteractionResult.FAIL;
        }

        /*
         * Otherwise place a new jam jar block.
         */
        BlockPlaceContext placeContext =
                new BlockPlaceContext(context);

        if (!placeContext.canPlace()) {
            return InteractionResult.FAIL;
        }

        BlockPos placePos =
                placeContext.getClickedPos();

        BlockState state =
                block.defaultBlockState()
                        .setValue(
                                JamBlock.FACING,
                                context.getHorizontalDirection()
                                        .getOpposite()
                        )
                        .setValue(
                                JamBlock.STACK,
                                1
                        );

        if (!level.isClientSide) {

            level.setBlock(
                    placePos,
                    state,
                    3
            );

            if (!context.getPlayer()
                    .getAbilities()
                    .instabuild) {

                context.getItemInHand()
                        .shrink(1);
            }
        }

        return InteractionResult.sidedSuccess(
                level.isClientSide
        );
    }
}