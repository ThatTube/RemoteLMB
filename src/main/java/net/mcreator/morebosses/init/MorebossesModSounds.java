
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.morebosses.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.ResourceLocation;

import net.mcreator.morebosses.MorebossesMod;

public class MorebossesModSounds {
	public static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MorebossesMod.MODID);
	public static final RegistryObject<SoundEvent> WIND_CHARGE = REGISTRY.register("wind_charge", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("morebosses", "wind_charge")));
}
