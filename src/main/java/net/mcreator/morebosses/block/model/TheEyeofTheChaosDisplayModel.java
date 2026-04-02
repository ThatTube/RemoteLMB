package net.mcreator.morebosses.block.model;

import software.bernie.geckolib.model.GeoModel;

import net.minecraft.resources.ResourceLocation;

import net.mcreator.morebosses.block.display.TheEyeofTheChaosDisplayItem;

public class TheEyeofTheChaosDisplayModel extends GeoModel<TheEyeofTheChaosDisplayItem> {
	@Override
	public ResourceLocation getAnimationResource(TheEyeofTheChaosDisplayItem animatable) {
		return new ResourceLocation("morebosses", "animations/eye_block.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(TheEyeofTheChaosDisplayItem animatable) {
		return new ResourceLocation("morebosses", "geo/eye_block.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(TheEyeofTheChaosDisplayItem entity) {
		return new ResourceLocation("morebosses", "textures/block/eyebigblock.png");
	}
}
