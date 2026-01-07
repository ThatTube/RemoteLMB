package net.mcreator.morebosses.entity.model;

import software.bernie.geckolib.model.GeoModel;

import net.minecraft.resources.ResourceLocation;

import net.mcreator.morebosses.entity.MaxolotEntity;

public class MaxolotModel extends GeoModel<MaxolotEntity> {
	@Override
	public ResourceLocation getAnimationResource(MaxolotEntity entity) {
		return new ResourceLocation("morebosses", "animations/maxolotl.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(MaxolotEntity entity) {
		return new ResourceLocation("morebosses", "geo/maxolotl.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(MaxolotEntity entity) {
		return new ResourceLocation("morebosses", "textures/entities/" + entity.getTexture() + ".png");
	}

}
