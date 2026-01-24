package net.mcreator.morebosses.block.renderer;

import software.bernie.geckolib.renderer.GeoItemRenderer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

import net.mcreator.morebosses.block.model.CopperDoorDisplayModel;
import net.mcreator.morebosses.block.display.CopperDoorDisplayItem;

public class CopperDoorDisplayItemRenderer extends GeoItemRenderer<CopperDoorDisplayItem> {
	public CopperDoorDisplayItemRenderer() {
		super(new CopperDoorDisplayModel());
	}

	@Override
	public RenderType getRenderType(CopperDoorDisplayItem animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
