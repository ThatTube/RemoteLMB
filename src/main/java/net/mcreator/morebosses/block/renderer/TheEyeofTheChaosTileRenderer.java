package net.mcreator.morebosses.block.renderer;

import software.bernie.geckolib.renderer.GeoBlockRenderer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

import net.mcreator.morebosses.block.model.TheEyeofTheChaosBlockModel;
import net.mcreator.morebosses.block.entity.TheEyeofTheChaosTileEntity;

public class TheEyeofTheChaosTileRenderer extends GeoBlockRenderer<TheEyeofTheChaosTileEntity> {
	public TheEyeofTheChaosTileRenderer() {
		super(new TheEyeofTheChaosBlockModel());
	}

	@Override
	public RenderType getRenderType(TheEyeofTheChaosTileEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
