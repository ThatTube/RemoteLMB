package net.mcreator.morebosses.block.renderer;

import software.bernie.geckolib.renderer.GeoItemRenderer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

import net.mcreator.morebosses.block.model.MonstrosityAltarDisplayModel;
import net.mcreator.morebosses.block.display.MonstrosityAltarDisplayItem;

public class MonstrosityAltarDisplayItemRenderer extends GeoItemRenderer<MonstrosityAltarDisplayItem> {
	public MonstrosityAltarDisplayItemRenderer() {
		super(new MonstrosityAltarDisplayModel());
	}

	@Override
	public RenderType getRenderType(MonstrosityAltarDisplayItem animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
