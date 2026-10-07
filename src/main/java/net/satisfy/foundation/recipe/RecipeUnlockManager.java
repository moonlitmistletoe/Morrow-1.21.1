package net.satisfy.foundation.recipe;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;
import java.util.Set;

/** Persists unlocked recipes per player via {@link RecipeUnlockSavedData}. */
public class RecipeUnlockManager {

    /** Heads up: currently stores the recipe TYPE key, not the recipe id. */
    public static void unlockRecipes(ServerPlayer player, List<Recipe<?>> recipes) {
        Set<ResourceLocation> unlocked = loadUnlockedRecipes(player);
        for (Recipe<?> recipe : recipes) {
            unlocked.add(BuiltInRegistries.RECIPE_TYPE.getKey(recipe.getType()));
        }
        saveUnlockedRecipes(player, unlocked);
    }

    /** Unlocks single recipes by id. */
    public static void unlockRecipeIds(ServerPlayer player, List<ResourceLocation> recipeIds) {
        Set<ResourceLocation> unlocked = loadUnlockedRecipes(player);
        unlocked.addAll(recipeIds);
        saveUnlockedRecipes(player, unlocked);
    }

    /** @return true if the player did not unlock this recipe yet */
    public static boolean isRecipeLocked(ServerPlayer player, ResourceLocation recipeId) {
        return !loadUnlockedRecipes(player).contains(recipeId);
    }

    /** Unlocked if either the recipe id or its whole recipe type is in the set. */
    public static boolean isUnlocked(Set<ResourceLocation> unlocked, RecipeHolder<?> recipe) {
        return unlocked.contains(recipe.id()) || unlocked.contains(BuiltInRegistries.RECIPE_TYPE.getKey(recipe.value().getType()));
    }

    public static boolean isUnlocked(ServerPlayer player, RecipeHolder<?> recipe) {
        return isUnlocked(loadUnlockedRecipes(player), recipe);
    }

    public static void saveUnlockedRecipes(ServerPlayer player, Set<ResourceLocation> recipes) {
        ServerLevel level = (ServerLevel) player.getCommandSenderWorld();
        RecipeUnlockSavedData data = level.getDataStorage()
                        .computeIfAbsent(RecipeUnlockSavedData.factory(), RecipeUnlockSavedData.DATA_NAME);
        data.setPlayerRecipes(player.getUUID(), recipes);
        RecipeUnlockSync.send(player, recipes);
    }

    public static Set<ResourceLocation> loadUnlockedRecipes(ServerPlayer player) {
        ServerLevel level = (ServerLevel) player.getCommandSenderWorld();
        RecipeUnlockSavedData data = level.getDataStorage()
                .computeIfAbsent(RecipeUnlockSavedData.factory(), RecipeUnlockSavedData.DATA_NAME);
        return data.getPlayerRecipes(player.getUUID());
    }
}
