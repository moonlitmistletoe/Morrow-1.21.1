package net.satisfy.foundation.food;

import com.mojang.datafixers.util.Pair;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Food that keeps the effects of whatever went into it. Can have multiple stages
 * (think: pie gets smaller each time you eat), the next stage is put back into
 * the inventory. Stage is stored as custom model data.
 */
public class IngredientEffectFoodItem extends Item implements IngredientEffectCarrier {

    private final int foodStages;

    public IngredientEffectFoodItem(Properties settings, int foodStages) {
        super(settings);
        this.foodStages = foodStages;
    }

    @Override
    public @NotNull ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity user) {
        if (!world.isClientSide) {
            List<Pair<MobEffectInstance, Float>> effects = IngredientEffects.getEffects(stack);
            for (Pair<MobEffectInstance, Float> effect : effects) {
                if (effect.getFirst() != null && world.random.nextFloat() < effect.getSecond()) {
                    user.addEffect(new MobEffectInstance(effect.getFirst()));
                }
            }
        }
        int stage = IngredientEffects.getStage(stack);
        int slot = -1;
        Inventory inv = null;
        if (user instanceof Player player && !player.isCreative()) {
            inv = player.getInventory();
            slot = inv.findSlotMatchingUnusedItem(stack);
        }
        ItemStack eaten = user.eat(world, stack);
        if (inv != null && stage < this.foodStages) {
            ItemStack next = IngredientEffects.setStage(new ItemStack(this), stage + 1);
            if (slot >= 0 && slot < inv.items.size()) {
                if (inv.getItem(slot).isEmpty()) {
                    inv.add(slot, next);
                    return eaten;
                }
            }
            int space = inv.getSlotWithRemainingSpace(next);
            if (space >= 0 && space < inv.items.size()) {
                inv.add(space, next);
            }
        }
        return eaten;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltipFlag) {
        IngredientEffects.getTooltip(itemStack, tooltipContext, list);
    }
}
