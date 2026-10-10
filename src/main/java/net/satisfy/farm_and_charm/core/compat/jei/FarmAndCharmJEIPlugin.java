package net.satisfy.farm_and_charm.core.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.satisfy.farm_and_charm.FarmAndCharm;
import net.satisfy.farm_and_charm.core.compat.jei.category.CookingPotCategory;
import net.satisfy.farm_and_charm.core.compat.jei.category.CraftingBowlCategory;
import net.satisfy.farm_and_charm.core.compat.jei.category.StoveCategory;
import net.satisfy.farm_and_charm.core.recipe.CookingPotRecipe;
import net.satisfy.farm_and_charm.core.recipe.CraftingBowlRecipe;
import net.satisfy.farm_and_charm.core.recipe.StoveRecipe;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import net.satisfy.farm_and_charm.core.registry.RecipeTypeRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@JeiPlugin
public class FarmAndCharmJEIPlugin implements IModPlugin {
    public static void addSlot(IRecipeLayoutBuilder builder, int x, int y, Ingredient ingredient) {
        builder.addSlot(RecipeIngredientRole.INPUT, x, y).addIngredients(ingredient);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new CookingPotCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new StoveCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new CraftingBowlCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager rm = Objects.requireNonNull(Minecraft.getInstance().level).getRecipeManager();
        registration.addRecipes(CookingPotCategory.COOKING_POT,
                rm.getAllRecipesFor(RecipeTypeRegistry.COOKING_POT_RECIPE_TYPE.get()).stream().map(RecipeHolder::value).toList());
        registration.addRecipes(StoveCategory.STOVE,
                rm.getAllRecipesFor(RecipeTypeRegistry.STOVE_RECIPE_TYPE.get()).stream().map(RecipeHolder::value).toList());
        registration.addRecipes(CraftingBowlCategory.DOUGHING,
                rm.getAllRecipesFor(RecipeTypeRegistry.CRAFTING_BOWL_RECIPE_TYPE.get()).stream().map(RecipeHolder::value).toList());
    }

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return FarmAndCharm.identifier("jei_plugin");
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(ObjectRegistry.COOKING_POT.get().asItem().getDefaultInstance(), CookingPotCategory.COOKING_POT);
        registration.addRecipeCatalyst(ObjectRegistry.CRAFTING_BOWL.get().asItem().getDefaultInstance(), CraftingBowlCategory.DOUGHING);
        registration.addRecipeCatalyst(ObjectRegistry.STOVE.get().asItem().getDefaultInstance(), StoveCategory.STOVE);
    }
}
