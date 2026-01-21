
/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.morebosses.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.item.enchantment.Enchantment;

import net.mcreator.morebosses.enchantment.ShowOffEnchantment;
import net.mcreator.morebosses.enchantment.MoocherEnchantment;
import net.mcreator.morebosses.MorebossesMod;

public class MorebossesModEnchantments {
	public static final DeferredRegister<Enchantment> REGISTRY = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, MorebossesMod.MODID);
	public static final RegistryObject<Enchantment> MOOCHER = REGISTRY.register("moocher", () -> new MoocherEnchantment());
	public static final RegistryObject<Enchantment> SHOW_OFF = REGISTRY.register("show_off", () -> new ShowOffEnchantment());
}
