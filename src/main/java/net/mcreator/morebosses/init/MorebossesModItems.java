
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.morebosses.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.common.ForgeSpawnEggItem;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.BlockItem;

import net.mcreator.morebosses.item.WitheredBoneItem;
import net.mcreator.morebosses.item.TankOfFuryItem;
import net.mcreator.morebosses.item.StoneCuirlassItem;
import net.mcreator.morebosses.item.ShieldBeaterItem;
import net.mcreator.morebosses.item.RawMagmaItem;
import net.mcreator.morebosses.item.NetherBricksStickItem;
import net.mcreator.morebosses.item.MONSTROUSFOOTWEARItem;
import net.mcreator.morebosses.item.GreenMagmaItem;
import net.mcreator.morebosses.item.GreenMagmaCreamItem;
import net.mcreator.morebosses.item.GongStickItem;
import net.mcreator.morebosses.item.GeradorDeWavesItem;
import net.mcreator.morebosses.item.EmptyTankItem;
import net.mcreator.morebosses.item.CuirlassSmithingUpgradeItem;
import net.mcreator.morebosses.item.CuirlassArmorItem;
import net.mcreator.morebosses.block.display.GongDisplayItem;
import net.mcreator.morebosses.MorebossesMod;

public class MorebossesModItems {
	public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, MorebossesMod.MODID);
	public static final RegistryObject<Item> GREEN_MAGMA_BLOCK = block(MorebossesModBlocks.GREEN_MAGMA_BLOCK);
	public static final RegistryObject<Item> GREEN_MAGMA_CREAM = REGISTRY.register("green_magma_cream", () -> new GreenMagmaCreamItem());
	public static final RegistryObject<Item> GREEN_MAGMA_BUCKET = REGISTRY.register("green_magma_bucket", () -> new GreenMagmaItem());
	public static final RegistryObject<Item> RAW_MAGMA_BUCKET = REGISTRY.register("raw_magma_bucket", () -> new RawMagmaItem());
	public static final RegistryObject<Item> COPPER_MONSTROSITY_SPAWN_EGG = REGISTRY.register("copper_monstrosity_spawn_egg", () -> new ForgeSpawnEggItem(MorebossesModEntities.COPPER_MONSTROSITY, -1, -1, new Item.Properties()));
	public static final RegistryObject<Item> MAXOLOT_SPAWN_EGG = REGISTRY.register("maxolot_spawn_egg", () -> new ForgeSpawnEggItem(MorebossesModEntities.MAXOLOT, -1, -1, new Item.Properties()));
	public static final RegistryObject<Item> GERADOR_DE_WAVES = REGISTRY.register("gerador_de_waves", () -> new GeradorDeWavesItem());
	public static final RegistryObject<Item> DRY_BONES_SPAWN_EGG = REGISTRY.register("dry_bones_spawn_egg", () -> new ForgeSpawnEggItem(MorebossesModEntities.DRY_BONES, -1, -1, new Item.Properties()));
	public static final RegistryObject<Item> STONE_CUIRLASS = REGISTRY.register("stone_cuirlass", () -> new StoneCuirlassItem());
	public static final RegistryObject<Item> TANK_OF_FURY = REGISTRY.register("tank_of_fury", () -> new TankOfFuryItem());
	public static final RegistryObject<Item> EMPTY_TANK = REGISTRY.register("empty_tank", () -> new EmptyTankItem());
	public static final RegistryObject<Item> CUIRLASS_ARMOR_HELMET = REGISTRY.register("cuirlass_armor_helmet", () -> new CuirlassArmorItem.Helmet());
	public static final RegistryObject<Item> CUIRLASS_ARMOR_CHESTPLATE = REGISTRY.register("cuirlass_armor_chestplate", () -> new CuirlassArmorItem.Chestplate());
	public static final RegistryObject<Item> CUIRLASS_ARMOR_LEGGINGS = REGISTRY.register("cuirlass_armor_leggings", () -> new CuirlassArmorItem.Leggings());
	public static final RegistryObject<Item> CUIRLASS_ARMOR_BOOTS = REGISTRY.register("cuirlass_armor_boots", () -> new CuirlassArmorItem.Boots());
	public static final RegistryObject<Item> CUIRLASS_SMITHING_UPGRADE = REGISTRY.register("cuirlass_smithing_upgrade", () -> new CuirlassSmithingUpgradeItem());
	public static final RegistryObject<Item> SHIELD_BEATER = REGISTRY.register("shield_beater", () -> new ShieldBeaterItem());
	public static final RegistryObject<Item> MINILOTL_SPAWN_EGG = REGISTRY.register("minilotl_spawn_egg", () -> new ForgeSpawnEggItem(MorebossesModEntities.MINILOTL, -1, -1, new Item.Properties()));
	public static final RegistryObject<Item> MONSTROUSFOOTWEAR_BOOTS = REGISTRY.register("monstrousfootwear_boots", () -> new MONSTROUSFOOTWEARItem.Boots());
	public static final RegistryObject<Item> NETHER_BRICKS_STICK = REGISTRY.register("nether_bricks_stick", () -> new NetherBricksStickItem());
	public static final RegistryObject<Item> GONG_STICK = REGISTRY.register("gong_stick", () -> new GongStickItem());
	public static final RegistryObject<Item> GONG = REGISTRY.register(MorebossesModBlocks.GONG.getId().getPath(), () -> new GongDisplayItem(MorebossesModBlocks.GONG.get(), new Item.Properties()));
	public static final RegistryObject<Item> POLISHED_CRACKED_BLACKSTONE_BLOCK = block(MorebossesModBlocks.POLISHED_CRACKED_BLACKSTONE_BLOCK);
	public static final RegistryObject<Item> WITHERED_BONE_BLOCK = block(MorebossesModBlocks.WITHERED_BONE_BLOCK);
	public static final RegistryObject<Item> WITHERED_BONE = REGISTRY.register("withered_bone", () -> new WitheredBoneItem());
	public static final RegistryObject<Item> CARVED_WITHERED_BONE_BLOCK = block(MorebossesModBlocks.CARVED_WITHERED_BONE_BLOCK);
	public static final RegistryObject<Item> CARVED_BONE_BLOCK = block(MorebossesModBlocks.CARVED_BONE_BLOCK);

	// Start of user code block custom items
	// End of user code block custom items
	private static RegistryObject<Item> block(RegistryObject<Block> block) {
		return REGISTRY.register(block.getId().getPath(), () -> new BlockItem(block.get(), new Item.Properties()));
	}
}
