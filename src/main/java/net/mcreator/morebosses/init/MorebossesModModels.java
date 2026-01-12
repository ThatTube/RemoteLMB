
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.morebosses.init;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.api.distmarker.Dist;

import net.mcreator.morebosses.client.model.Modelshockwave;
import net.mcreator.morebosses.client.model.ModelCustomModel;
import net.mcreator.morebosses.client.model.ModelCustomArmor;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = {Dist.CLIENT})
public class MorebossesModModels {
	@SubscribeEvent
	public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(ModelCustomModel.LAYER_LOCATION, ModelCustomModel::createBodyLayer);
		event.registerLayerDefinition(Modelshockwave.LAYER_LOCATION, Modelshockwave::createBodyLayer);
		event.registerLayerDefinition(ModelCustomArmor.LAYER_LOCATION, ModelCustomArmor::createBodyLayer);
	}
}
