
/*
 * MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.morebosses.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ItemBlockRenderTypes;

import net.mcreator.morebosses.fluid.RawMagmaFluid;
import net.mcreator.morebosses.fluid.GreenMagmaFluid;
import net.mcreator.morebosses.fluid.BobFluid;
import net.mcreator.morebosses.MorebossesMod;

public class MorebossesModFluids {
	public static final DeferredRegister<Fluid> REGISTRY = DeferredRegister.create(ForgeRegistries.FLUIDS, MorebossesMod.MODID);
	public static final RegistryObject<FlowingFluid> GREEN_MAGMA = REGISTRY.register("green_magma", () -> new GreenMagmaFluid.Source());
	public static final RegistryObject<FlowingFluid> FLOWING_GREEN_MAGMA = REGISTRY.register("flowing_green_magma", () -> new GreenMagmaFluid.Flowing());
	public static final RegistryObject<FlowingFluid> RAW_MAGMA = REGISTRY.register("raw_magma", () -> new RawMagmaFluid.Source());
	public static final RegistryObject<FlowingFluid> FLOWING_RAW_MAGMA = REGISTRY.register("flowing_raw_magma", () -> new RawMagmaFluid.Flowing());
	public static final RegistryObject<FlowingFluid> BOB = REGISTRY.register("bob", () -> new BobFluid.Source());
	public static final RegistryObject<FlowingFluid> FLOWING_BOB = REGISTRY.register("flowing_bob", () -> new BobFluid.Flowing());

	@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
	public static class FluidsClientSideHandler {
		@SubscribeEvent
		public static void clientSetup(FMLClientSetupEvent event) {
			ItemBlockRenderTypes.setRenderLayer(GREEN_MAGMA.get(), RenderType.translucent());
			ItemBlockRenderTypes.setRenderLayer(FLOWING_GREEN_MAGMA.get(), RenderType.translucent());
			ItemBlockRenderTypes.setRenderLayer(RAW_MAGMA.get(), RenderType.translucent());
			ItemBlockRenderTypes.setRenderLayer(FLOWING_RAW_MAGMA.get(), RenderType.translucent());
			ItemBlockRenderTypes.setRenderLayer(BOB.get(), RenderType.translucent());
			ItemBlockRenderTypes.setRenderLayer(FLOWING_BOB.get(), RenderType.translucent());
		}
	}
}
