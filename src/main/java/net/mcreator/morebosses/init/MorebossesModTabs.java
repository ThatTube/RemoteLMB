
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
			}).withSearchBar().build());
	public static final RegistryObject<CreativeModeTab> LMB_ITENS = REGISTRY.register("lmb_itens",
			() -> CreativeModeTab.builder().title(Component.translatable("item_group.morebosses.lmb_itens")).icon(() -> new ItemStack(MorebossesModItems.GREEN_MAGMA_CREAM.get())).displayItems((parameters, tabData) -> {
				tabData.accept(MorebossesModItems.GREEN_MAGMA_CREAM.get());
				tabData.accept(MorebossesModItems.GREEN_MAGMA_BUCKET.get());
				tabData.accept(MorebossesModItems.RAW_MAGMA_BUCKET.get());
			}).withSearchBar().withTabsBefore(LMB_BLOCKS.getId()).build());
}
