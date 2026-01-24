
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.morebosses.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.network.chat.Component;
import net.minecraft.core.registries.Registries;

import net.mcreator.morebosses.MorebossesMod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class MorebossesModTabs {
	public static final DeferredRegister<CreativeModeTab> REGISTRY = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MorebossesMod.MODID);
	public static final RegistryObject<CreativeModeTab> LMB_BLOCKS = REGISTRY.register("lmb_blocks",
			() -> CreativeModeTab.builder().title(Component.translatable("item_group.morebosses.lmb_blocks")).icon(() -> new ItemStack(MorebossesModBlocks.GREEN_MAGMA_BLOCK.get())).displayItems((parameters, tabData) -> {
				tabData.accept(MorebossesModBlocks.GREEN_MAGMA_BLOCK.get().asItem());
				tabData.accept(MorebossesModBlocks.GONG.get().asItem());
				tabData.accept(MorebossesModBlocks.POLISHED_CRACKED_BLACKSTONE_BLOCK.get().asItem());
				tabData.accept(MorebossesModBlocks.WITHERED_BONE_BLOCK.get().asItem());
				tabData.accept(MorebossesModBlocks.CARVED_WITHERED_BONE_BLOCK.get().asItem());
				tabData.accept(MorebossesModBlocks.CARVED_BONE_BLOCK.get().asItem());
				tabData.accept(MorebossesModBlocks.RAW_MAGMA_BLOCK.get().asItem());
				tabData.accept(MorebossesModBlocks.SEAT.get().asItem());
				tabData.accept(MorebossesModBlocks.SOUL_LIGHT.get().asItem());
				tabData.accept(MorebossesModBlocks.BLUE_SOUL_LIGHT.get().asItem());
				tabData.accept(MorebossesModBlocks.COPPER_FALL.get().asItem());
				tabData.accept(MorebossesModBlocks.COPPER_LAVA.get().asItem());
				tabData.accept(MorebossesModBlocks.COPPER_BOOOM.get().asItem());
				tabData.accept(MorebossesModBlocks.INDESTRUCTIBLE_BLOCK.get().asItem());
				tabData.accept(MorebossesModBlocks.COPPER_GRATES.get().asItem());
				tabData.accept(MorebossesModBlocks.COPPER_PRESSURE_PLATE.get().asItem());
			}).withSearchBar().build());
	public static final RegistryObject<CreativeModeTab> LMB_ITENS = REGISTRY.register("lmb_itens",
			() -> CreativeModeTab.builder().title(Component.translatable("item_group.morebosses.lmb_itens")).icon(() -> new ItemStack(MorebossesModItems.GREEN_MAGMA_CREAM.get())).displayItems((parameters, tabData) -> {
				tabData.accept(MorebossesModItems.GREEN_MAGMA_CREAM.get());
				tabData.accept(MorebossesModItems.GREEN_MAGMA_BUCKET.get());
				tabData.accept(MorebossesModItems.RAW_MAGMA_BUCKET.get());
				tabData.accept(MorebossesModItems.STONE_CUIRLASS.get());
				tabData.accept(MorebossesModItems.TANK_OF_FURY.get());
				tabData.accept(MorebossesModItems.EMPTY_TANK.get());
				tabData.accept(MorebossesModItems.CUIRLASS_ARMOR_HELMET.get());
				tabData.accept(MorebossesModItems.CUIRLASS_ARMOR_CHESTPLATE.get());
				tabData.accept(MorebossesModItems.CUIRLASS_ARMOR_LEGGINGS.get());
				tabData.accept(MorebossesModItems.CUIRLASS_ARMOR_BOOTS.get());
				tabData.accept(MorebossesModItems.CUIRLASS_SMITHING_UPGRADE.get());
				tabData.accept(MorebossesModItems.SHIELD_BEATER.get());
				tabData.accept(MorebossesModItems.MONSTROUSFOOTWEAR_BOOTS.get());
				tabData.accept(MorebossesModItems.NETHER_BRICKS_STICK.get());
				tabData.accept(MorebossesModItems.GONG_STICK.get());
				tabData.accept(MorebossesModItems.WITHERED_BONE.get());
				tabData.accept(MorebossesModItems.RAW_MAGMA_CREAM.get());
				tabData.accept(MorebossesModItems.BONE_CARVER.get());
				tabData.accept(MorebossesModItems.FRIED_CHAYOTE.get());
				tabData.accept(MorebossesModItems.BOILED_EGG.get());
				tabData.accept(MorebossesModItems.FRIED_EGG.get());
				tabData.accept(MorebossesModBlocks.CHAYOTE.get().asItem());
				tabData.accept(MorebossesModItems.POT.get());
				tabData.accept(MorebossesModItems.POT_WITH_FLOUR.get());
				tabData.accept(MorebossesModItems.FLESH.get());
				tabData.accept(MorebossesModItems.PIPE_WRENCH.get());
				tabData.accept(MorebossesModItems.MACABRE_SCYTHE.get());
				tabData.accept(MorebossesModItems.MONSTROSITY_EYE.get());
				tabData.accept(MorebossesModItems.MMA_EYE.get());
				tabData.accept(MorebossesModItems.GEAR.get());
				tabData.accept(MorebossesModItems.COPPER_WIRES.get());
				tabData.accept(MorebossesModItems.FUSE.get());
				tabData.accept(MorebossesModItems.MOTHERBOARD.get());
				tabData.accept(MorebossesModItems.COPPER_HAMMER.get());
				tabData.accept(MorebossesModItems.COPPER_SOUP.get());
			}).withSearchBar().withTabsBefore(LMB_BLOCKS.getId()).build());
	public static final RegistryObject<CreativeModeTab> LMB_MOBS = REGISTRY.register("lmb_mobs",
			() -> CreativeModeTab.builder().title(Component.translatable("item_group.morebosses.lmb_mobs")).icon(() -> new ItemStack(MorebossesModItems.COPPER_MONSTROSITY_SPAWN_EGG.get())).displayItems((parameters, tabData) -> {
				tabData.accept(MorebossesModItems.COPPER_MONSTROSITY_SPAWN_EGG.get());
				tabData.accept(MorebossesModItems.MAXOLOT_SPAWN_EGG.get());
				tabData.accept(MorebossesModItems.DRY_BONES_SPAWN_EGG.get());
				tabData.accept(MorebossesModItems.MINILOTL_SPAWN_EGG.get());
				tabData.accept(MorebossesModItems.BEGGAR_WOLF_SPAWN_EGG.get());
				tabData.accept(MorebossesModItems.ENGINEER_SPAWN_EGG.get());
				tabData.accept(MorebossesModItems.BRUTE_ENGINEER_SPAWN_EGG.get());
				tabData.accept(MorebossesModItems.TURRET_SPAWN_EGG.get());
				tabData.accept(MorebossesModItems.TALL_ENGINEER_SPAWN_EGG.get());
				tabData.accept(MorebossesModItems.OIL_ENGINEER_SPAWN_EGG.get());
				tabData.accept(MorebossesModItems.COPPER_GRABLER_SPAWN_EGG.get());
			}).withSearchBar().withTabsBefore(LMB_ITENS.getId()).build());

	@SubscribeEvent
	public static void buildTabContentsVanilla(BuildCreativeModeTabContentsEvent tabData) {
		if (tabData.getTabKey() == CreativeModeTabs.OP_BLOCKS) {
			if (tabData.hasPermissions()) {
				tabData.accept(MorebossesModBlocks.COPPER_STRUCTURE_DETECT.get().asItem());
			}
		}
	}
}
