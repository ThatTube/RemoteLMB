package net.mcreator.morebosses.block.renderer;

import software.bernie.geckolib.renderer.GeoBlockRenderer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

import net.mcreator.morebosses.block.model.EnergyAltarBlockModel;
import net.mcreator.morebosses.block.entity.EnergyAltarTileEntity;

public class EnergyAltarTileRenderer extends GeoBlockRenderer<EnergyAltarTileEntity> {
	public EnergyAltarTileRenderer() {
		super(new EnergyAltarBlockModel());
	}

	@Override
	public RenderType getRenderType(EnergyAltarTileEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
