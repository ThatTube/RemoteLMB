
package net.mcreator.morebosses.init;

import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;

import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.IModPlugin;

import java.util.List;

@JeiPlugin
public class MorebossesModJeiInformation implements IModPlugin {
	@Override
	public ResourceLocation getPluginUid() {
		return new ResourceLocation("morebosses:information");
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		registration.addIngredientInfo(
				List.of(new ItemStack(MorebossesModItems.MONSTROSITY_EYE.get()), new ItemStack(MorebossesModItems.MMA_EYE.get()), new ItemStack(MorebossesModItems.LUSH_EYE.get()), new ItemStack(MorebossesModItems.OLD_EYE.get())),
				VanillaTypes.ITEM_STACK, Component.translatable("jei.morebosses.eyesinformation"));
	}
}
