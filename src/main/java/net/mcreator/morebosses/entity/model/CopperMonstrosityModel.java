package net.mcreator.morebosses.entity.model;

import software.bernie.geckolib.model.GeoModel;

import net.minecraft.resources.ResourceLocation;

import net.mcreator.morebosses.entity.CopperMonstrosityEntity;

public class CopperMonstrosityModel extends GeoModel<CopperMonstrosityEntity> {
	@Override
	public ResourceLocation getAnimationResource(CopperMonstrosityEntity entity) {
		return new ResourceLocation("morebosses", "animations/copper_monstrosity.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(CopperMonstrosityEntity entity) {
		return new ResourceLocation("morebosses", "geo/copper_monstrosity.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(CopperMonstrosityEntity entity) {
		return new ResourceLocation("morebosses", "textures/entities/" + entity.getTexture() + ".png");
	}

}
