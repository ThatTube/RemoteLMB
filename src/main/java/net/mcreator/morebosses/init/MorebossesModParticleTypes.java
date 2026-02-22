
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.morebosses.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.particles.ParticleType;

import net.mcreator.morebosses.MorebossesMod;

public class MorebossesModParticleTypes {
	public static final DeferredRegister<ParticleType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, MorebossesMod.MODID);
	public static final RegistryObject<SimpleParticleType> DOOM = REGISTRY.register("doom", () -> new SimpleParticleType(false));
}
