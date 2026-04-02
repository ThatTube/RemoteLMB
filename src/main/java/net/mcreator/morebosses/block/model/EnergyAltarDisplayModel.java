package net.mcreator.morebosses.block.model;

import software.bernie.geckolib.model.GeoModel;

import net.minecraft.resources.ResourceLocation;

import net.mcreator.morebosses.block.display.EnergyAltarDisplayItem;

public class EnergyAltarDisplayModel extends GeoModel<EnergyAltarDisplayItem> {
	@Override
	public ResourceLocation getAnimationResource(EnergyAltarDisplayItem animatable) {
		return new ResourceLocation("morebosses", "animations/ealtar.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(EnergyAltarDisplayItem animatable) {
		return new ResourceLocation("morebosses", "geo/ealtar.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(EnergyAltarDisplayItem entity) {
		return new ResourceLocation("morebosses", "textures/block/energyaltar.png");
	}
}
