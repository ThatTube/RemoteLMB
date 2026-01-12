
/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.morebosses.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.effect.MobEffect;

import net.mcreator.morebosses.potion.PanicMobEffect;
import net.mcreator.morebosses.potion.HeavyMobEffect;
import net.mcreator.morebosses.MorebossesMod;

public class MorebossesModMobEffects {
	public static final DeferredRegister<MobEffect> REGISTRY = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, MorebossesMod.MODID);
	public static final RegistryObject<MobEffect> HEAVY = REGISTRY.register("heavy", () -> new HeavyMobEffect());
	public static final RegistryObject<MobEffect> PANIC = REGISTRY.register("panic", () -> new PanicMobEffect());
}
