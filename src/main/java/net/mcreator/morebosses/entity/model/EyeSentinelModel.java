package net.mcreator.morebosses.entity.model;

import software.bernie.geckolib.model.GeoModel;

import net.minecraft.resources.ResourceLocation;

import net.mcreator.morebosses.entity.EyeSentinelEntity;

public class EyeSentinelModel extends GeoModel<EyeSentinelEntity> {
	@Override
	public ResourceLocation getAnimationResource(EyeSentinelEntity entity) {
		return new ResourceLocation("morebosses", "animations/eye_sentinel.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(EyeSentinelEntity entity) {
		return new ResourceLocation("morebosses", "geo/eye_sentinel.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(EyeSentinelEntity entity) {
		return new ResourceLocation("morebosses", "textures/entities/" + entity.getTexture() + ".png");
	}

}
