package net.satisfy.foundation.food;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.foundation.food.IngredientEffects;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Stores the ingredient effects of a placed food. Hunger gets filtered out,
 * nobody wants that.
 */
public class IngredientEffectFoodBlockEntity extends BlockEntity {
    public static final String STORED_EFFECTS_KEY = "StoredEffects";
    private List<Pair<MobEffectInstance, Float>> effects;

    public IngredientEffectFoodBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void addEffects(List<Pair<MobEffectInstance, Float>> effects) {
        this.effects = effects.stream()
                .filter(p -> p.getFirst().getEffect() != MobEffects.HUNGER)
                .collect(Collectors.toList());
    }

    public List<Pair<MobEffectInstance, Float>> getEffects() {
        return effects != null ? effects : Collections.emptyList();
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        this.effects = IngredientEffects.fromNbt(tag.getList(STORED_EFFECTS_KEY, 10));
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        if (effects == null) return;
        ListTag list = new ListTag();
        for (Pair<MobEffectInstance, Float> effect : effects) {
            list.add(IngredientEffects.createNbt((short) BuiltInRegistries.MOB_EFFECT.asHolderIdMap().getId(effect.getFirst().getEffect()), effect));
        }
        tag.put(STORED_EFFECTS_KEY, list);
    }
}
