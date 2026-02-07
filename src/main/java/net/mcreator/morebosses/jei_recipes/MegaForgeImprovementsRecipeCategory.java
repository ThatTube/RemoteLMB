
package net.mcreator.morebosses.jei_recipes;

import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;

import net.mcreator.morebosses.init.MorebossesModJeiPlugin;
import net.mcreator.morebosses.init.MorebossesModBlocks;

import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.constants.VanillaTypes;

public class MegaForgeImprovementsRecipeCategory implements IRecipeCategory<MegaForgeImprovementsRecipe> {
	public final static ResourceLocation UID = new ResourceLocation("morebosses", "mega_forge_improvements");
	public final static ResourceLocation TEXTURE = new ResourceLocation("morebosses", "textures/screens/works554.png");
	private final IDrawable background;
	private final IDrawable icon;

	public MegaForgeImprovementsRecipeCategory(IGuiHelper helper) {
		this.background = helper.createDrawable(TEXTURE, 0, 0, 172, 70);
		this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(MorebossesModBlocks.MEGA_FORGE.get().asItem()));
	}

	@Override
	public mezz.jei.api.recipe.RecipeType<MegaForgeImprovementsRecipe> getRecipeType() {
		return MorebossesModJeiPlugin.MegaForgeImprovements_Type;
	}

	@Override
	public Component getTitle() {
		return Component.literal("Mega Forge Improvements");
	}

	@Override
	public IDrawable getBackground() {
		return this.background;
	}

	@Override
	public IDrawable getIcon() {
		return this.icon;
	}

	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, MegaForgeImprovementsRecipe recipe, IFocusGroup focuses) {
		builder.addSlot(RecipeIngredientRole.INPUT, 25, 38).addIngredients(recipe.getIngredients().get(0));
		builder.addSlot(RecipeIngredientRole.INPUT, 62, 38).addIngredients(recipe.getIngredients().get(1));
		builder.addSlot(RecipeIngredientRole.OUTPUT, 113, 38).addItemStack(recipe.getResultItem(null));
	}
}
