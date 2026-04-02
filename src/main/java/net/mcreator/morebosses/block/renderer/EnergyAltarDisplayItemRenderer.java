package net.mcreator.morebosses.block.renderer;

import software.bernie.geckolib.renderer.GeoItemRenderer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

import net.mcreator.morebosses.block.model.EnergyAltarDisplayModel;
import net.mcreator.morebosses.block.display.EnergyAltarDisplayItem;

public class EnergyAltarDisplayItemRenderer extends GeoItemRenderer<EnergyAltarDisplayItem> {
	public EnergyAltarDisplayItemRenderer() {
		super(new EnergyAltarDisplayModel());
	}

	@Override
	public RenderType getRenderType(EnergyAltarDisplayItem animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}
}
