package net.mcreator.morebosses.item.model;

import software.bernie.geckolib.model.GeoModel;

import net.minecraft.resources.ResourceLocation;

import net.mcreator.morebosses.item.CuirlassGloveItem;

public class CuirlassGloveItemModel extends GeoModel<CuirlassGloveItem> {
	@Override
	public ResourceLocation getAnimationResource(CuirlassGloveItem animatable) {
		return new ResourceLocation("morebosses", "animations/cuirlass_glove.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(CuirlassGloveItem animatable) {
		return new ResourceLocation("morebosses", "geo/cuirlass_glove.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(CuirlassGloveItem animatable) {
		return new ResourceLocation("morebosses", "textures/item/cg.png");
	}
}
