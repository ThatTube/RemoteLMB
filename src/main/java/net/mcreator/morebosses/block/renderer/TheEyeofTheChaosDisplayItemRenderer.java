package net.mcreator.morebosses.block.renderer;

import software.bernie.geckolib.renderer.GeoItemRenderer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

import net.mcreator.morebosses.block.model.TheEyeofTheChaosDisplayModel;
import net.mcreator.morebosses.block.display.TheEyeofTheChaosDisplayItem;

public class TheEyeofTheChaosDisplayItemRenderer extends GeoItemRenderer<TheEyeofTheChaosDisplayItem> {
	public TheEyeofTheChaosDisplayItemRenderer() {
		super(new TheEyeofTheChaosDisplayModel());
	}

	@Override
	public RenderType getRenderType(TheEyeofTheChaosDisplayItem animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
