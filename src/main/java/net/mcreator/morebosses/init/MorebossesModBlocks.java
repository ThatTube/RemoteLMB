
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.morebosses.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.level.block.Block;

import net.mcreator.morebosses.block.GreenMagmaBlockBlock;
import net.mcreator.morebosses.MorebossesMod;

public class MorebossesModBlocks {
	public static final DeferredRegister<Block> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, MorebossesMod.MODID);
	public static final RegistryObject<Block> GREEN_MAGMA_BLOCK = REGISTRY.register("green_magma_block", () -> new GreenMagmaBlockBlock());
	// Start of user code block custom blocks
	// End of user code block custom blocks
}
