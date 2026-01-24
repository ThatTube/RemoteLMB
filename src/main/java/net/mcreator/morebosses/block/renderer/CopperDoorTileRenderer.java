package net.mcreator.morebosses.block.renderer;

import software.bernie.geckolib.renderer.GeoBlockRenderer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

import net.mcreator.morebosses.block.model.CopperDoorBlockModel;
import net.mcreator.morebosses.block.entity.CopperDoorTileEntity;

public class CopperDoorTileRenderer extends GeoBlockRenderer<CopperDoorTileEntity> {
	public CopperDoorTileRenderer() {
		super(new CopperDoorBlockModel());
	}

	@Override
	public RenderType getRenderType(CopperDoorTileEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
