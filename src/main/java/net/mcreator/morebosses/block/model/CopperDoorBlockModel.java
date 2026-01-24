package net.mcreator.morebosses.block.model;

import software.bernie.geckolib.model.GeoModel;

import net.minecraft.resources.ResourceLocation;

import net.mcreator.morebosses.block.entity.CopperDoorTileEntity;

public class CopperDoorBlockModel extends GeoModel<CopperDoorTileEntity> {
	@Override
	public ResourceLocation getAnimationResource(CopperDoorTileEntity animatable) {
		final int blockstate = animatable.blockstateNew;
		if (blockstate == 1)
			return new ResourceLocation("morebosses", "animations/copper_door.animation.json");
		if (blockstate == 2)
			return new ResourceLocation("morebosses", "animations/copper_door.animation.json");
		return new ResourceLocation("morebosses", "animations/copper_door.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(CopperDoorTileEntity animatable) {
		final int blockstate = animatable.blockstateNew;
		if (blockstate == 1)
			return new ResourceLocation("morebosses", "geo/copper_door.geo.json");
		if (blockstate == 2)
			return new ResourceLocation("morebosses", "geo/copper_door.geo.json");
		return new ResourceLocation("morebosses", "geo/copper_door.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(CopperDoorTileEntity animatable) {
		final int blockstate = animatable.blockstateNew;
		if (blockstate == 1)
			return new ResourceLocation("morebosses", "textures/block/copper_door.png");
		if (blockstate == 2)
			return new ResourceLocation("morebosses", "textures/block/copper_door.png");
		return new ResourceLocation("morebosses", "textures/block/copper_door.png");
	}
}
