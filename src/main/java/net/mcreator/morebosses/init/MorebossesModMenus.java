
/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.morebosses.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.common.extensions.IForgeMenuType;

import net.minecraft.world.inventory.MenuType;

import net.mcreator.morebosses.world.inventory.WorkshopGUIMenu;
import net.mcreator.morebosses.MorebossesMod;

public class MorebossesModMenus {
	public static final DeferredRegister<MenuType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MorebossesMod.MODID);
	public static final RegistryObject<MenuType<WorkshopGUIMenu>> WORKSHOP_GUI = REGISTRY.register("workshop_gui", () -> IForgeMenuType.create(WorkshopGUIMenu::new));
}
