package net.mcreator.morebosses.block.model;

import software.bernie.geckolib.model.GeoModel;

import net.minecraft.resources.ResourceLocation;

import net.mcreator.morebosses.block.display.CopperDoorDisplayItem;

public class CopperDoorDisplayModel extends GeoModel<CopperDoorDisplayItem> {
	@Override
	public ResourceLocation getAnimationResource(CopperDoorDisplayItem animatable) {
		return new ResourceLocation("morebosses", "animations/copper_door.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(CopperDoorDisplayItem animatable) {
		return new ResourceLocation("morebosses", "geo/copper_door.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(CopperDoorDisplayItem entity) {
		return new ResourceLocation("morebosses", "textures/block/copper_door.png");
	}
}
