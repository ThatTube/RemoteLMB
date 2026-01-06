
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.morebosses.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.level.block.Block;

import net.mcreator.morebosses.block.RawMagmaBlock;
import net.mcreator.morebosses.block.GreenMagmaBlockBlock;
import net.mcreator.morebosses.block.GreenMagmaBlock;
import net.mcreator.morebosses.MorebossesMod;

public class MorebossesModBlocks {
	public static final DeferredRegister<Block> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, MorebossesMod.MODID);
	public static final RegistryObject<Block> GREEN_MAGMA_BLOCK = REGISTRY.register("green_magma_block", () -> new GreenMagmaBlockBlock());
	public static final RegistryObject<Block> GREEN_MAGMA = REGISTRY.register("green_magma", () -> new GreenMagmaBlock());
	public static final RegistryObject<Block> RAW_MAGMA = REGISTRY.register("raw_magma", () -> new RawMagmaBlock());
	// Start of user code block custom blocks
	// End of user code block custom blocks
}
