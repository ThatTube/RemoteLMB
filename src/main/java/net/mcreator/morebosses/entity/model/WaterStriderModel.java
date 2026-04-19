package net.mcreator.morebosses.entity.model;

import software.bernie.geckolib.model.GeoModel;

import net.minecraft.resources.ResourceLocation;

import net.mcreator.morebosses.entity.WaterStriderEntity;

public class WaterStriderModel extends GeoModel<WaterStriderEntity> {
	@Override
	public ResourceLocation getAnimationResource(WaterStriderEntity entity) {
		return new ResourceLocation("morebosses", "animations/wstrider.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(WaterStriderEntity entity) {
		return new ResourceLocation("morebosses", "geo/wstrider.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(WaterStriderEntity entity) {
		return new ResourceLocation("morebosses", "textures/entities/" + entity.getTexture() + ".png");
	}

}
