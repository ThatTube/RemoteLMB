package net.mcreator.morebosses.block.model;

import software.bernie.geckolib.model.GeoModel;

import net.minecraft.resources.ResourceLocation;

import net.mcreator.morebosses.block.entity.TheEyeofTheChaosTileEntity;

public class TheEyeofTheChaosBlockModel extends GeoModel<TheEyeofTheChaosTileEntity> {
	@Override
	public ResourceLocation getAnimationResource(TheEyeofTheChaosTileEntity animatable) {
		return new ResourceLocation("morebosses", "animations/eye_block.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(TheEyeofTheChaosTileEntity animatable) {
		return new ResourceLocation("morebosses", "geo/eye_block.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(TheEyeofTheChaosTileEntity animatable) {
		return new ResourceLocation("morebosses", "textures/block/eyebigblock.png");
	}
}
