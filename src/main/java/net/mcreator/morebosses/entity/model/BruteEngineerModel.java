package net.mcreator.morebosses.entity.model;

import software.bernie.geckolib.model.GeoModel;

import net.minecraft.resources.ResourceLocation;

import net.mcreator.morebosses.entity.BruteEngineerEntity;

public class BruteEngineerModel extends GeoModel<BruteEngineerEntity> {
	@Override
	public ResourceLocation getAnimationResource(BruteEngineerEntity entity) {
		return new ResourceLocation("morebosses", "animations/brute_engineer.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(BruteEngineerEntity entity) {
		return new ResourceLocation("morebosses", "geo/brute_engineer.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(BruteEngineerEntity entity) {
		return new ResourceLocation("morebosses", "textures/entities/" + entity.getTexture() + ".png");
	}

}
