package net.mcreator.morebosses.block.model;

import software.bernie.geckolib.model.GeoModel;

import net.minecraft.resources.ResourceLocation;

import net.mcreator.morebosses.block.entity.MonstrosityAltarTileEntity;

public class MonstrosityAltarBlockModel extends GeoModel<MonstrosityAltarTileEntity> {
	@Override
	public ResourceLocation getAnimationResource(MonstrosityAltarTileEntity animatable) {
		return new ResourceLocation("morebosses", "animations/monstrosity_summon.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(MonstrosityAltarTileEntity animatable) {
		return new ResourceLocation("morebosses", "geo/monstrosity_summon.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(MonstrosityAltarTileEntity animatable) {
		return new ResourceLocation("morebosses", "textures/block/monstruos_block.png");
	}
}
