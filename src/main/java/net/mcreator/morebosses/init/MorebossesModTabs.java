
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.morebosses.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.network.chat.Component;
import net.minecraft.core.registries.Registries;

import net.mcreator.morebosses.MorebossesMod;

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
			}).withSearchBar().withTabsBefore(LMB_BLOCKS.getId()).build());
	public static final RegistryObject<CreativeModeTab> LMB_MOBS = REGISTRY.register("lmb_mobs",
			() -> CreativeModeTab.builder().title(Component.translatable("item_group.morebosses.lmb_mobs")).icon(() -> new ItemStack(MorebossesModItems.COPPER_MONSTROSITY_SPAWN_EGG.get())).displayItems((parameters, tabData) -> {
				tabData.accept(MorebossesModItems.COPPER_MONSTROSITY_SPAWN_EGG.get());
				tabData.accept(MorebossesModItems.MAXOLOT_SPAWN_EGG.get());
				tabData.accept(MorebossesModItems.DRY_BONES_SPAWN_EGG.get());
				tabData.accept(MorebossesModItems.MINILOTL_SPAWN_EGG.get());
			}).withSearchBar().withTabsBefore(LMB_ITENS.getId()).build());
}
