package net.mcreator.morebosses.block.model;

import software.bernie.geckolib.model.GeoModel;

import net.minecraft.resources.ResourceLocation;

import net.mcreator.morebosses.block.display.MonstrosityAltarDisplayItem;

public class MonstrosityAltarDisplayModel extends GeoModel<MonstrosityAltarDisplayItem> {
	@Override
	public ResourceLocation getAnimationResource(MonstrosityAltarDisplayItem animatable) {
		return new ResourceLocation("morebosses", "animations/monstrosity_summon.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(MonstrosityAltarDisplayItem animatable) {
		return new ResourceLocation("morebosses", "geo/monstrosity_summon.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(MonstrosityAltarDisplayItem entity) {
		return new ResourceLocation("morebosses", "textures/block/monstruos_block.png");
	}
}
