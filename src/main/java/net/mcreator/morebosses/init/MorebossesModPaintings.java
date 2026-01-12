
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.morebosses.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.entity.decoration.PaintingVariant;

import net.mcreator.morebosses.MorebossesMod;

public class MorebossesModPaintings {
	public static final DeferredRegister<PaintingVariant> REGISTRY = DeferredRegister.create(ForgeRegistries.PAINTING_VARIANTS, MorebossesMod.MODID);
	public static final RegistryObject<PaintingVariant> MONSTROUS = REGISTRY.register("monstrous", () -> new PaintingVariant(64, 64));
}
