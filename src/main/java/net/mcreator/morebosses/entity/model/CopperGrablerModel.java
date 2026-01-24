package net.mcreator.morebosses.entity.model;

import software.bernie.geckolib.model.GeoModel;

import net.minecraft.resources.ResourceLocation;

import net.mcreator.morebosses.entity.CopperGrablerEntity;

public class CopperGrablerModel extends GeoModel<CopperGrablerEntity> {
	@Override
	public ResourceLocation getAnimationResource(CopperGrablerEntity entity) {
		return new ResourceLocation("morebosses", "animations/copper_grabber.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(CopperGrablerEntity entity) {
		return new ResourceLocation("morebosses", "geo/copper_grabber.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(CopperGrablerEntity entity) {
		return new ResourceLocation("morebosses", "textures/entities/" + entity.getTexture() + ".png");
	}

}
