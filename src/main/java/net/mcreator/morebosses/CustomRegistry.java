package net.mcreator.morebosses;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.common.Mod;

import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.item.Item;
import net.minecraft.tags.TagKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;

import net.mcreator.morebosses.item.MonstrosityEyeItem;

@Mod.EventBusSubscriber(modid = MorebossesMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CustomRegistry {

	// Criamos nosso próprio registro
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MorebossesMod.MODID);

	// Criamos a tag manualmente
	public static final TagKey<Structure> TEST_STRUCTURE = TagKey.create(Registries.STRUCTURE, new ResourceLocation(MorebossesMod.MODID, "test"));

	// Registramos o item
	public static final RegistryObject<Item> MONSTROSITY_EYE = ITEMS.register("monstrosity_eye", () -> new MonstrosityEyeItem(TEST_STRUCTURE));

	// Método para registrar
	public static void register() {
		ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
}
