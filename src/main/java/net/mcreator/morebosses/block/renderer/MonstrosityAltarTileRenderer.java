package net.mcreator.morebosses.block.renderer;

import software.bernie.geckolib.renderer.GeoBlockRenderer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

import net.mcreator.morebosses.block.model.MonstrosityAltarBlockModel;
import net.mcreator.morebosses.block.entity.MonstrosityAltarTileEntity;

public class MonstrosityAltarTileRenderer extends GeoBlockRenderer<MonstrosityAltarTileEntity> {
	public MonstrosityAltarTileRenderer() {
		super(new MonstrosityAltarBlockModel());
	}

	@Override
	public RenderType getRenderType(MonstrosityAltarTileEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
