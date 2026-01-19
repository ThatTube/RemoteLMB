package net.mcreator.morebosses.entity.model;

import software.bernie.geckolib.model.GeoModel;

import net.minecraft.resources.ResourceLocation;

import net.mcreator.morebosses.entity.TallEngineerEntity;

public class TallEngineerModel extends GeoModel<TallEngineerEntity> {
	@Override
	public ResourceLocation getAnimationResource(TallEngineerEntity entity) {
		return new ResourceLocation("morebosses", "animations/tall_engineer.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(TallEngineerEntity entity) {
		return new ResourceLocation("morebosses", "geo/tall_engineer.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(TallEngineerEntity entity) {
		return new ResourceLocation("morebosses", "textures/entities/" + entity.getTexture() + ".png");
	}

}
