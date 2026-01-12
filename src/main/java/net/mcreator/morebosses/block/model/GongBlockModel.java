package net.mcreator.morebosses.block.model;

import software.bernie.geckolib.model.GeoModel;

import net.minecraft.resources.ResourceLocation;

import net.mcreator.morebosses.block.entity.GongTileEntity;

public class GongBlockModel extends GeoModel<GongTileEntity> {
	@Override
	public ResourceLocation getAnimationResource(GongTileEntity animatable) {
		return new ResourceLocation("morebosses", "animations/gong.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(GongTileEntity animatable) {
		return new ResourceLocation("morebosses", "geo/gong.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(GongTileEntity animatable) {
		return new ResourceLocation("morebosses", "textures/block/gong.png");
	}
}
