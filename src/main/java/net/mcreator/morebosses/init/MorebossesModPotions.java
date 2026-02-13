
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.morebosses.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.effect.MobEffectInstance;

import net.mcreator.morebosses.MorebossesMod;

public class MorebossesModPotions {
	public static final DeferredRegister<Potion> REGISTRY = DeferredRegister.create(ForgeRegistries.POTIONS, MorebossesMod.MODID);
	public static final RegistryObject<Potion> GREASE = REGISTRY.register("grease", () -> new Potion(new MobEffectInstance(MorebossesModMobEffects.ENGINE_BOOST.get(), 3600, 0, false, true)));
	public static final RegistryObject<Potion> OIL = REGISTRY.register("oil", () -> new Potion(new MobEffectInstance(MorebossesModMobEffects.STICKY.get(), 3600, 0, false, true)));
	public static final RegistryObject<Potion> HARD_SKIN_POTION = REGISTRY.register("hard_skin_potion", () -> new Potion(new MobEffectInstance(MorebossesModMobEffects.HARD_SKIN.get(), 3600, 0, false, true)));
}
