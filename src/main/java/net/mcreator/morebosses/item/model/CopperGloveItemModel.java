package net.mcreator.morebosses.item.model;

import software.bernie.geckolib.model.GeoModel;

import net.minecraft.resources.ResourceLocation;

import net.mcreator.morebosses.item.CopperGloveItem;

public class CopperGloveItemModel extends GeoModel<CopperGloveItem> {
	@Override
	public ResourceLocation getAnimationResource(CopperGloveItem animatable) {
		return new ResourceLocation("morebosses", "animations/cuirlass_glove.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(CopperGloveItem animatable) {
		return new ResourceLocation("morebosses", "geo/cuirlass_glove.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(CopperGloveItem animatable) {
		return new ResourceLocation("morebosses", "textures/item/copper_gloves.png");
	}
}
