
/*
 * MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.morebosses.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.fluids.FluidType;

import net.mcreator.morebosses.fluid.types.RawMagmaFluidType;
import net.mcreator.morebosses.fluid.types.GreenMagmaFluidType;
import net.mcreator.morebosses.MorebossesMod;

public class MorebossesModFluidTypes {
	public static final DeferredRegister<FluidType> REGISTRY = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, MorebossesMod.MODID);
	public static final RegistryObject<FluidType> GREEN_MAGMA_TYPE = REGISTRY.register("green_magma", () -> new GreenMagmaFluidType());
	public static final RegistryObject<FluidType> RAW_MAGMA_TYPE = REGISTRY.register("raw_magma", () -> new RawMagmaFluidType());
}
