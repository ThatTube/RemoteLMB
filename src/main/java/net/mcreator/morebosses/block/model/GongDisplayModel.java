package net.mcreator.morebosses.block.model;

import software.bernie.geckolib.model.GeoModel;

import net.minecraft.resources.ResourceLocation;

import net.mcreator.morebosses.block.display.GongDisplayItem;

public class GongDisplayModel extends GeoModel<GongDisplayItem> {
	@Override
	public ResourceLocation getAnimationResource(GongDisplayItem animatable) {
		return new ResourceLocation("morebosses", "animations/gong.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(GongDisplayItem animatable) {
		return new ResourceLocation("morebosses", "geo/gong.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(GongDisplayItem entity) {
		return new ResourceLocation("morebosses", "textures/block/gong.png");
	}
}
