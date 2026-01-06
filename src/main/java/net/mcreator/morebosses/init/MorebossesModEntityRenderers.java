
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.morebosses.init;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.api.distmarker.Dist;

import net.mcreator.morebosses.client.renderer.WindBurstRenderer;
import net.mcreator.morebosses.client.renderer.ShockWaveRenderer;
import net.mcreator.morebosses.client.renderer.CopperMonstrosityRenderer;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class MorebossesModEntityRenderers {
	@SubscribeEvent
	public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(MorebossesModEntities.COPPER_MONSTROSITY.get(), CopperMonstrosityRenderer::new);
		event.registerEntityRenderer(MorebossesModEntities.SHOCK_WAVE.get(), ShockWaveRenderer::new);
		event.registerEntityRenderer(MorebossesModEntities.WIND_BURST.get(), WindBurstRenderer::new);
	}
}
