package net.mcreator.morebosses.item.model;

import software.bernie.geckolib.model.GeoModel;

import net.minecraft.resources.ResourceLocation;

import net.mcreator.morebosses.item.RobotGloveItem;

public class RobotGloveItemModel extends GeoModel<RobotGloveItem> {
	@Override
	public ResourceLocation getAnimationResource(RobotGloveItem animatable) {
		return new ResourceLocation("morebosses", "animations/cuirlass_glove.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(RobotGloveItem animatable) {
		return new ResourceLocation("morebosses", "geo/cuirlass_glove.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(RobotGloveItem animatable) {
		return new ResourceLocation("morebosses", "textures/item/robot_glove.png");
	}
}
