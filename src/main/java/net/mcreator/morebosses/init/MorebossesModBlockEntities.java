
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.morebosses.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.Block;

import net.mcreator.morebosses.block.entity.MonstrosityAltarTileEntity;
import net.mcreator.morebosses.block.entity.GongTileEntity;
import net.mcreator.morebosses.block.entity.CopperDoorTileEntity;
import net.mcreator.morebosses.MorebossesMod;

public class MorebossesModBlockEntities {
	public static final DeferredRegister<BlockEntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MorebossesMod.MODID);
	public static final RegistryObject<BlockEntityType<GongTileEntity>> GONG = REGISTRY.register("gong", () -> BlockEntityType.Builder.of(GongTileEntity::new, MorebossesModBlocks.GONG.get()).build(null));
	public static final RegistryObject<BlockEntityType<MonstrosityAltarTileEntity>> MONSTROSITY_ALTAR = REGISTRY.register("monstrosity_altar",
			() -> BlockEntityType.Builder.of(MonstrosityAltarTileEntity::new, MorebossesModBlocks.MONSTROSITY_ALTAR.get()).build(null));
	public static final RegistryObject<BlockEntityType<CopperDoorTileEntity>> COPPER_DOOR = REGISTRY.register("copper_door", () -> BlockEntityType.Builder.of(CopperDoorTileEntity::new, MorebossesModBlocks.COPPER_DOOR.get()).build(null));

	// Start of user code block custom block entities
	// End of user code block custom block entities
	private static RegistryObject<BlockEntityType<?>> register(String registryname, RegistryObject<Block> block, BlockEntityType.BlockEntitySupplier<?> supplier) {
		return REGISTRY.register(registryname, () -> BlockEntityType.Builder.of(supplier, block.get()).build(null));
	}
}
