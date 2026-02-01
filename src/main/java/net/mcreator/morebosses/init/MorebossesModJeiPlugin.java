
package net.mcreator.morebosses.init;

import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.Minecraft;

import net.mcreator.morebosses.jei_recipes.MegaForgeImprovementsRecipeCategory;
import net.mcreator.morebosses.jei_recipes.MegaForgeImprovementsRecipe;

import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.IModPlugin;

import java.util.Objects;
import java.util.List;

@JeiPlugin
public class MorebossesModJeiPlugin implements IModPlugin {
	public static mezz.jei.api.recipe.RecipeType<MegaForgeImprovementsRecipe> MegaForgeImprovements_Type = new mezz.jei.api.recipe.RecipeType<>(MegaForgeImprovementsRecipeCategory.UID, MegaForgeImprovementsRecipe.class);

	@Override
	public ResourceLocation getPluginUid() {
		return new ResourceLocation("morebosses:jei_plugin");
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registration) {
		registration.addRecipeCategories(new MegaForgeImprovementsRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		RecipeManager recipeManager = Objects.requireNonNull(Minecraft.getInstance().level).getRecipeManager();
		List<MegaForgeImprovementsRecipe> MegaForgeImprovementsRecipes = recipeManager.getAllRecipesFor(MegaForgeImprovementsRecipe.Type.INSTANCE);
		registration.addRecipes(MegaForgeImprovements_Type, MegaForgeImprovementsRecipes);
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		registration.addRecipeCatalyst(new ItemStack(MorebossesModBlocks.MEGA_FORGE.get().asItem()), MegaForgeImprovements_Type);
	}
}
