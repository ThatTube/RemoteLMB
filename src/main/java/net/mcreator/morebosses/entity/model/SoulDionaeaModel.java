package net.mcreator.morebosses.entity.model;

import software.bernie.geckolib.model.GeoModel;

import net.minecraft.resources.ResourceLocation;

import net.mcreator.morebosses.entity.SoulDionaeaEntity;

public class SoulDionaeaModel extends GeoModel<SoulDionaeaEntity> {
	@Override
	public ResourceLocation getAnimationResource(SoulDionaeaEntity entity) {
		return new ResourceLocation("morebosses", "animations/dionaea.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(SoulDionaeaEntity entity) {
		return new ResourceLocation("morebosses", "geo/dionaea.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(SoulDionaeaEntity entity) {
		return new ResourceLocation("morebosses", "textures/entities/" + entity.getTexture() + ".png");
	}

}
