package net.mcreator.morebosses.entity.model;

import software.bernie.geckolib.model.GeoModel;

import net.minecraft.resources.ResourceLocation;

import net.mcreator.morebosses.entity.BeggarWolfEntity;

public class BeggarWolfModel extends GeoModel<BeggarWolfEntity> {
	@Override
	public ResourceLocation getAnimationResource(BeggarWolfEntity entity) {
		return new ResourceLocation("morebosses", "animations/pidao.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(BeggarWolfEntity entity) {
		return new ResourceLocation("morebosses", "geo/pidao.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(BeggarWolfEntity entity) {
		return new ResourceLocation("morebosses", "textures/entities/" + entity.getTexture() + ".png");
	}

}
