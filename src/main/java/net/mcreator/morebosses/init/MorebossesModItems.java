
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
import net.mcreator.morebosses.item.WitherRingItem;
import net.mcreator.morebosses.item.TankOfFuryItem;
import net.mcreator.morebosses.item.StoneCuirlassItem;
import net.mcreator.morebosses.item.ShieldBeaterItem;
import net.mcreator.morebosses.item.RobotGloveItem;
import net.mcreator.morebosses.item.RingItem;
import net.mcreator.morebosses.item.RawMagmaItem;
import net.mcreator.morebosses.item.RawMagmaCreamItem;
import net.mcreator.morebosses.item.PotWithFlourItem;
import net.mcreator.morebosses.item.PotItem;
import net.mcreator.morebosses.item.PoisonRingItem;
import net.mcreator.morebosses.item.PipeWrenchItem;
import net.mcreator.morebosses.item.OxidationDustItem;
import net.mcreator.morebosses.item.NetheriteScimitarItem;
import net.mcreator.morebosses.item.NetherBricksStickItem;
import net.mcreator.morebosses.item.MotherboardItem;
import net.mcreator.morebosses.item.MonstrosityEyeItem;
import net.mcreator.morebosses.item.MacabreScytheItem;
import net.mcreator.morebosses.item.MONSTROUSFOOTWEARItem;
import net.mcreator.morebosses.item.MMAGloveItem;
import net.mcreator.morebosses.item.MMAEyeItem;
import net.mcreator.morebosses.item.GreenMagmaItem;
import net.mcreator.morebosses.item.GreenMagmaCreamItem;
import net.mcreator.morebosses.item.GongStickItem;
import net.mcreator.morebosses.item.GeradorDeWavesItem;
import net.mcreator.morebosses.item.GearItem;
import net.mcreator.morebosses.item.FuseItem;
import net.mcreator.morebosses.item.FriedEggItem;
import net.mcreator.morebosses.item.FriedChayoteItem;
import net.mcreator.morebosses.item.FlourItem;
import net.mcreator.morebosses.item.FleshItem;
import net.mcreator.morebosses.item.FireRingItem;
import net.mcreator.morebosses.item.EmptyTankItem;
import net.mcreator.morebosses.item.CuirlassSmithingUpgradeItem;
import net.mcreator.morebosses.item.CuirlassGloveItem;
import net.mcreator.morebosses.item.CuirlassArmorItem;
import net.mcreator.morebosses.item.CopperWiresItem;
import net.mcreator.morebosses.item.CopperSoupItem;
import net.mcreator.morebosses.item.CopperHammerItem;
import net.mcreator.morebosses.item.CopperGloveItem;
import net.mcreator.morebosses.item.BoneCarverItem;
import net.mcreator.morebosses.item.BoiledEggItem;
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
	public static final RegistryObject<Item> RAW_MAGMA_BLOCK = block(MorebossesModBlocks.RAW_MAGMA_BLOCK);
	public static final RegistryObject<Item> SEAT = block(MorebossesModBlocks.SEAT);
	public static final RegistryObject<Item> SOUL_DIONAEA_SPAWN_EGG = REGISTRY.register("soul_dionaea_spawn_egg", () -> new ForgeSpawnEggItem(MorebossesModEntities.SOUL_DIONAEA, -1, -1, new Item.Properties()));
	public static final RegistryObject<Item> SOUL_LIGHT = block(MorebossesModBlocks.SOUL_LIGHT);
	public static final RegistryObject<Item> BLUE_SOUL_LIGHT = block(MorebossesModBlocks.BLUE_SOUL_LIGHT);
	public static final RegistryObject<Item> RAW_MAGMA_CREAM = REGISTRY.register("raw_magma_cream", () -> new RawMagmaCreamItem());
	public static final RegistryObject<Item> BONE_CARVER = REGISTRY.register("bone_carver", () -> new BoneCarverItem());
	public static final RegistryObject<Item> FRIED_CHAYOTE = REGISTRY.register("fried_chayote", () -> new FriedChayoteItem());
	public static final RegistryObject<Item> BOILED_EGG = REGISTRY.register("boiled_egg", () -> new BoiledEggItem());
	public static final RegistryObject<Item> FRIED_EGG = REGISTRY.register("fried_egg", () -> new FriedEggItem());
	public static final RegistryObject<Item> CHAYOTE = block(MorebossesModBlocks.CHAYOTE);
	public static final RegistryObject<Item> POT = REGISTRY.register("pot", () -> new PotItem());
	public static final RegistryObject<Item> BEGGAR_WOLF_SPAWN_EGG = REGISTRY.register("beggar_wolf_spawn_egg", () -> new ForgeSpawnEggItem(MorebossesModEntities.BEGGAR_WOLF, -1, -1, new Item.Properties()));
	public static final RegistryObject<Item> FLOUR = REGISTRY.register("flour", () -> new FlourItem());
	public static final RegistryObject<Item> POT_WITH_FLOUR = REGISTRY.register("pot_with_flour", () -> new PotWithFlourItem());
	public static final RegistryObject<Item> FLESH = REGISTRY.register("flesh", () -> new FleshItem());
	public static final RegistryObject<Item> ENGINEER_SPAWN_EGG = REGISTRY.register("engineer_spawn_egg", () -> new ForgeSpawnEggItem(MorebossesModEntities.ENGINEER, -1, -1, new Item.Properties()));
	public static final RegistryObject<Item> BRUTE_ENGINEER_SPAWN_EGG = REGISTRY.register("brute_engineer_spawn_egg", () -> new ForgeSpawnEggItem(MorebossesModEntities.BRUTE_ENGINEER, -1, -1, new Item.Properties()));
	public static final RegistryObject<Item> TURRET_SPAWN_EGG = REGISTRY.register("turret_spawn_egg", () -> new ForgeSpawnEggItem(MorebossesModEntities.TURRET, -1, -1, new Item.Properties()));
	public static final RegistryObject<Item> TALL_ENGINEER_SPAWN_EGG = REGISTRY.register("tall_engineer_spawn_egg", () -> new ForgeSpawnEggItem(MorebossesModEntities.TALL_ENGINEER, -1, -1, new Item.Properties()));
	public static final RegistryObject<Item> OIL_ENGINEER_SPAWN_EGG = REGISTRY.register("oil_engineer_spawn_egg", () -> new ForgeSpawnEggItem(MorebossesModEntities.OIL_ENGINEER, -1, -1, new Item.Properties()));
	public static final RegistryObject<Item> PIPE_WRENCH = REGISTRY.register("pipe_wrench", () -> new PipeWrenchItem());
	public static final RegistryObject<Item> MACABRE_SCYTHE = REGISTRY.register("macabre_scythe", () -> new MacabreScytheItem());
	public static final RegistryObject<Item> MONSTROSITY_EYE = REGISTRY.register("monstrosity_eye", () -> new MonstrosityEyeItem());
	public static final RegistryObject<Item> COPPER_FALL = block(MorebossesModBlocks.COPPER_FALL);
	public static final RegistryObject<Item> COPPER_LAVA = block(MorebossesModBlocks.COPPER_LAVA);
	public static final RegistryObject<Item> COPPER_BOOOM = block(MorebossesModBlocks.COPPER_BOOOM);
	public static final RegistryObject<Item> MMA_EYE = REGISTRY.register("mma_eye", () -> new MMAEyeItem());
	public static final RegistryObject<Item> GEAR = REGISTRY.register("gear", () -> new GearItem());
	public static final RegistryObject<Item> INDESTRUCTIBLE_BLOCK = block(MorebossesModBlocks.INDESTRUCTIBLE_BLOCK);
	public static final RegistryObject<Item> COPPER_WIRES = REGISTRY.register("copper_wires", () -> new CopperWiresItem());
	public static final RegistryObject<Item> FUSE = REGISTRY.register("fuse", () -> new FuseItem());
	public static final RegistryObject<Item> MOTHERBOARD = REGISTRY.register("motherboard", () -> new MotherboardItem());
	public static final RegistryObject<Item> COPPER_HAMMER = REGISTRY.register("copper_hammer", () -> new CopperHammerItem());
	public static final RegistryObject<Item> COPPER_SOUP = REGISTRY.register("copper_soup", () -> new CopperSoupItem());
	public static final RegistryObject<Item> OXIDATION_DUST = REGISTRY.register("oxidation_dust", () -> new OxidationDustItem());
	public static final RegistryObject<Item> COPPER_GRATES = block(MorebossesModBlocks.COPPER_GRATES);
	public static final RegistryObject<Item> COPPER_PRESSURE_PLATE = block(MorebossesModBlocks.COPPER_PRESSURE_PLATE);
	public static final RegistryObject<Item> COPPER_STRUCTURE_DETECT = block(MorebossesModBlocks.COPPER_STRUCTURE_DETECT);
	public static final RegistryObject<Item> COPPER_GRABLER_SPAWN_EGG = REGISTRY.register("copper_grabler_spawn_egg", () -> new ForgeSpawnEggItem(MorebossesModEntities.COPPER_GRABLER, -1, -1, new Item.Properties()));
	public static final RegistryObject<Item> NETHERITE_SCIMITAR = REGISTRY.register("netherite_scimitar", () -> new NetheriteScimitarItem());
	public static final RegistryObject<Item> CUIRLASS_GLOVE = REGISTRY.register("cuirlass_glove", () -> new CuirlassGloveItem());
	public static final RegistryObject<Item> MEGA_FORGE = block(MorebossesModBlocks.MEGA_FORGE);
	public static final RegistryObject<Item> ROBOT_GLOVE = REGISTRY.register("robot_glove", () -> new RobotGloveItem());
	public static final RegistryObject<Item> ROBOT_WHALE_SPAWN_EGG = REGISTRY.register("robot_whale_spawn_egg", () -> new ForgeSpawnEggItem(MorebossesModEntities.ROBOT_WHALE, -1, -1, new Item.Properties()));
	public static final RegistryObject<Item> COPPER_GLOVE = REGISTRY.register("copper_glove", () -> new CopperGloveItem());
	public static final RegistryObject<Item> SHRIMP_SPAWN_EGG = REGISTRY.register("shrimp_spawn_egg", () -> new ForgeSpawnEggItem(MorebossesModEntities.SHRIMP, -1, -1, new Item.Properties()));
	public static final RegistryObject<Item> MMA_GLOVE = REGISTRY.register("mma_glove", () -> new MMAGloveItem());
	public static final RegistryObject<Item> WITHER_RING = REGISTRY.register("wither_ring", () -> new WitherRingItem());
	public static final RegistryObject<Item> RING = REGISTRY.register("ring", () -> new RingItem());
	public static final RegistryObject<Item> FIRE_RING = REGISTRY.register("fire_ring", () -> new FireRingItem());
	public static final RegistryObject<Item> POISON_RING = REGISTRY.register("poison_ring", () -> new PoisonRingItem());
	public static final RegistryObject<Item> PIGLIN_BOULUS_SPAWN_EGG = REGISTRY.register("piglin_boulus_spawn_egg", () -> new ForgeSpawnEggItem(MorebossesModEntities.PIGLIN_BOULUS, -1, -1, new Item.Properties()));
	public static final RegistryObject<Item> PIGLIN_BOXER_SPAWN_EGG = REGISTRY.register("piglin_boxer_spawn_egg", () -> new ForgeSpawnEggItem(MorebossesModEntities.PIGLIN_BOXER, -1, -1, new Item.Properties()));

	// Start of user code block custom items
	// End of user code block custom items
	private static RegistryObject<Item> block(RegistryObject<Block> block) {
		return REGISTRY.register(block.getId().getPath(), () -> new BlockItem(block.get(), new Item.Properties()));
	}
}
