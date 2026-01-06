
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.morebosses.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.BlockItem;

import net.mcreator.morebosses.item.RawMagmaItem;
import net.mcreator.morebosses.item.GreenMagmaItem;
import net.mcreator.morebosses.item.GreenMagmaCreamItem;
import net.mcreator.morebosses.MorebossesMod;

public class MorebossesModItems {
	public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, MorebossesMod.MODID);
	public static final RegistryObject<Item> GREEN_MAGMA_BLOCK = block(MorebossesModBlocks.GREEN_MAGMA_BLOCK);
	public static final RegistryObject<Item> GREEN_MAGMA_CREAM = REGISTRY.register("green_magma_cream", () -> new GreenMagmaCreamItem());
	public static final RegistryObject<Item> GREEN_MAGMA_BUCKET = REGISTRY.register("green_magma_bucket", () -> new GreenMagmaItem());
	public static final RegistryObject<Item> RAW_MAGMA_BUCKET = REGISTRY.register("raw_magma_bucket", () -> new RawMagmaItem());
	public static final RegistryObject<Item> WEWE = block(MorebossesModBlocks.WEWE);

	// Start of user code block custom items
	// End of user code block custom items
	private static RegistryObject<Item> block(RegistryObject<Block> block) {
		return REGISTRY.register(block.getId().getPath(), () -> new BlockItem(block.get(), new Item.Properties()));
	}
}
