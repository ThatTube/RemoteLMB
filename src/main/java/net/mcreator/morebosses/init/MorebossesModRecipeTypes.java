package net.mcreator.morebosses.init;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.IEventBus;

import net.minecraft.world.item.crafting.RecipeSerializer;

import net.mcreator.morebosses.jei_recipes.MegaForgeImprovementsRecipe;
import net.mcreator.morebosses.MorebossesMod;

@Mod.EventBusSubscriber(modid = MorebossesMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MorebossesModRecipeTypes {
	public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, "morebosses");

	@SubscribeEvent
	public static void register(FMLConstructModEvent event) {
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		event.enqueueWork(() -> {
			SERIALIZERS.register(bus);
			SERIALIZERS.register("mega_forge_improvements", () -> MegaForgeImprovementsRecipe.Serializer.INSTANCE);
		});
	}
}
