package net.mcreator.morebosses.entity.model;

import software.bernie.geckolib.model.GeoModel;

import net.minecraft.resources.ResourceLocation;

import net.mcreator.morebosses.entity.OilEngineerEntity;

public class OilEngineerModel extends GeoModel<OilEngineerEntity> {
	@Override
	public ResourceLocation getAnimationResource(OilEngineerEntity entity) {
		return new ResourceLocation("morebosses", "animations/oil_engineer.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(OilEngineerEntity entity) {
		return new ResourceLocation("morebosses", "geo/oil_engineer.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(OilEngineerEntity entity) {
		return new ResourceLocation("morebosses", "textures/entities/" + entity.getTexture() + ".png");
	}

}
