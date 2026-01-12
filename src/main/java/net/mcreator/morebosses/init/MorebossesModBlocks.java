
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.morebosses.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.level.block.Block;

import net.mcreator.morebosses.block.WitheredBoneBlockBlock;
import net.mcreator.morebosses.block.RawMagmaBlock;
import net.mcreator.morebosses.block.PolishedCrackedBlackstoneBlockBlock;
import net.mcreator.morebosses.block.GreenMagmaBlockBlock;
import net.mcreator.morebosses.block.GreenMagmaBlock;
import net.mcreator.morebosses.block.GongBlock;
import net.mcreator.morebosses.block.CarvedWitheredBoneBlockBlock;
import net.mcreator.morebosses.block.CarvedBoneBlockBlock;
import net.mcreator.morebosses.MorebossesMod;

public class MorebossesModBlocks {
	public static final DeferredRegister<Block> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, MorebossesMod.MODID);
	public static final RegistryObject<Block> GREEN_MAGMA_BLOCK = REGISTRY.register("green_magma_block", () -> new GreenMagmaBlockBlock());
	public static final RegistryObject<Block> GREEN_MAGMA = REGISTRY.register("green_magma", () -> new GreenMagmaBlock());
	public static final RegistryObject<Block> RAW_MAGMA = REGISTRY.register("raw_magma", () -> new RawMagmaBlock());
	public static final RegistryObject<Block> GONG = REGISTRY.register("gong", () -> new GongBlock());
	public static final RegistryObject<Block> POLISHED_CRACKED_BLACKSTONE_BLOCK = REGISTRY.register("polished_cracked_blackstone_block", () -> new PolishedCrackedBlackstoneBlockBlock());
	public static final RegistryObject<Block> WITHERED_BONE_BLOCK = REGISTRY.register("withered_bone_block", () -> new WitheredBoneBlockBlock());
	public static final RegistryObject<Block> CARVED_WITHERED_BONE_BLOCK = REGISTRY.register("carved_withered_bone_block", () -> new CarvedWitheredBoneBlockBlock());
	public static final RegistryObject<Block> CARVED_BONE_BLOCK = REGISTRY.register("carved_bone_block", () -> new CarvedBoneBlockBlock());
	// Start of user code block custom blocks
	// End of user code block custom blocks
}
