
/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.morebosses.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.effect.MobEffect;

import net.mcreator.morebosses.potion.StickyMobEffect;
import net.mcreator.morebosses.potion.SampleMobEffect;
import net.mcreator.morebosses.potion.PanicMobEffect;
import net.mcreator.morebosses.potion.HeavyMobEffect;
import net.mcreator.morebosses.potion.FrenzyMobEffect;
import net.mcreator.morebosses.potion.FlammableMobEffect;
import net.mcreator.morebosses.potion.FlamingIgnitionMobEffect;
import net.mcreator.morebosses.potion.EngineBoostMobEffect;
import net.mcreator.morebosses.MorebossesMod;

public class MorebossesModMobEffects {
	public static final DeferredRegister<MobEffect> REGISTRY = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, MorebossesMod.MODID);
	public static final RegistryObject<MobEffect> HEAVY = REGISTRY.register("heavy", () -> new HeavyMobEffect());
	public static final RegistryObject<MobEffect> PANIC = REGISTRY.register("panic", () -> new PanicMobEffect());
	public static final RegistryObject<MobEffect> STICKY = REGISTRY.register("sticky", () -> new StickyMobEffect());
	public static final RegistryObject<MobEffect> ENGINE_BOOST = REGISTRY.register("engine_boost", () -> new EngineBoostMobEffect());
	public static final RegistryObject<MobEffect> FLAMING_IGNITION = REGISTRY.register("flaming_ignition", () -> new FlamingIgnitionMobEffect());
	public static final RegistryObject<MobEffect> FLAMMABLE = REGISTRY.register("flammable", () -> new FlammableMobEffect());
	public static final RegistryObject<MobEffect> SAMPLE = REGISTRY.register("sample", () -> new SampleMobEffect());
	public static final RegistryObject<MobEffect> FRENZY = REGISTRY.register("frenzy", () -> new FrenzyMobEffect());
}
