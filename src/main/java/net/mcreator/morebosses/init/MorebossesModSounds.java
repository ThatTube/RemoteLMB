
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
	public static final RegistryObject<SoundEvent> PIGLINBOULUSSCREAM = REGISTRY.register("piglinboulusscream", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("morebosses", "piglinboulusscream")));
	public static final RegistryObject<SoundEvent> COPPER_PLACEHOLDER = REGISTRY.register("copper_placeholder", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("morebosses", "copper_placeholder")));
	public static final RegistryObject<SoundEvent> TAUNT = REGISTRY.register("taunt", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("morebosses", "taunt")));
	public static final RegistryObject<SoundEvent> ENDERTP = REGISTRY.register("endertp", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("morebosses", "endertp")));
	public static final RegistryObject<SoundEvent> ENDERBOMBSHOOT = REGISTRY.register("enderbombshoot", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("morebosses", "enderbombshoot")));
	public static final RegistryObject<SoundEvent> ENDERSLAM = REGISTRY.register("enderslam", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("morebosses", "enderslam")));
	public static final RegistryObject<SoundEvent> ENDERBLOCK = REGISTRY.register("enderblock", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("morebosses", "enderblock")));
	public static final RegistryObject<SoundEvent> ENDHURTS = REGISTRY.register("endhurts", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation("morebosses", "endhurts")));
}
