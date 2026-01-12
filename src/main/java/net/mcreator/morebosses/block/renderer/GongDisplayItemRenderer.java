package net.mcreator.morebosses.block.renderer;

import software.bernie.geckolib.renderer.GeoItemRenderer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

import net.mcreator.morebosses.block.model.GongDisplayModel;
import net.mcreator.morebosses.block.display.GongDisplayItem;

public class GongDisplayItemRenderer extends GeoItemRenderer<GongDisplayItem> {
	public GongDisplayItemRenderer() {
		super(new GongDisplayModel());
	}

	@Override
	public RenderType getRenderType(GongDisplayItem animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
