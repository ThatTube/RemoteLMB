package net.mcreator.morebosses.block.model;

import software.bernie.geckolib.model.GeoModel;

import net.minecraft.resources.ResourceLocation;

import net.mcreator.morebosses.block.entity.EnergyAltarTileEntity;

public class EnergyAltarBlockModel extends GeoModel<EnergyAltarTileEntity> {
	@Override
	public ResourceLocation getAnimationResource(EnergyAltarTileEntity animatable) {
		return new ResourceLocation("morebosses", "animations/ealtar.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(EnergyAltarTileEntity animatable) {
		return new ResourceLocation("morebosses", "geo/ealtar.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(EnergyAltarTileEntity animatable) {
		return new ResourceLocation("morebosses", "textures/block/energyaltar.png");
	}
}
