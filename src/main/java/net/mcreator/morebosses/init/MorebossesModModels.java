
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.morebosses.init;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.api.distmarker.Dist;

import net.mcreator.morebosses.client.model.Modelwither_gloveproject;
import net.mcreator.morebosses.client.model.Modelshockwave;
import net.mcreator.morebosses.client.model.Modelbracadeira;
import net.mcreator.morebosses.client.model.ModelShrimp;
import net.mcreator.morebosses.client.model.ModelCustomModel;
import net.mcreator.morebosses.client.model.ModelCustomArmor;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = {Dist.CLIENT})
public class MorebossesModModels {
	@SubscribeEvent
	public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(ModelCustomArmor.LAYER_LOCATION, ModelCustomArmor::createBodyLayer);
		event.registerLayerDefinition(Modelwither_gloveproject.LAYER_LOCATION, Modelwither_gloveproject::createBodyLayer);
		event.registerLayerDefinition(ModelCustomModel.LAYER_LOCATION, ModelCustomModel::createBodyLayer);
		event.registerLayerDefinition(Modelshockwave.LAYER_LOCATION, Modelshockwave::createBodyLayer);
		event.registerLayerDefinition(Modelbracadeira.LAYER_LOCATION, Modelbracadeira::createBodyLayer);
		event.registerLayerDefinition(ModelShrimp.LAYER_LOCATION, ModelShrimp::createBodyLayer);
	}
}
