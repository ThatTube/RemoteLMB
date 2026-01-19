package net.mcreator.morebosses.entity.model;

import software.bernie.geckolib.model.GeoModel;

import net.minecraft.resources.ResourceLocation;

import net.mcreator.morebosses.entity.EngineerEntity;

public class EngineerModel extends GeoModel<EngineerEntity> {
	@Override
	public ResourceLocation getAnimationResource(EngineerEntity entity) {
		return new ResourceLocation("morebosses", "animations/engineer.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(EngineerEntity entity) {
		return new ResourceLocation("morebosses", "geo/engineer.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(EngineerEntity entity) {
		return new ResourceLocation("morebosses", "textures/entities/" + entity.getTexture() + ".png");
	}

}
