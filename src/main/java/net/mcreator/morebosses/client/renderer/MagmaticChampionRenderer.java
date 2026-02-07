
package net.mcreator.morebosses.client.renderer;

import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.cache.object.BakedGeoModel;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

import net.mcreator.morebosses.entity.model.MagmaticChampionModel;
import net.mcreator.morebosses.entity.layer.MagmaticChampionLayer;
import net.mcreator.morebosses.entity.MagmaticChampionEntity;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;

public class MagmaticChampionRenderer extends GeoEntityRenderer<MagmaticChampionEntity> {
	public MagmaticChampionRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new MagmaticChampionModel());
		this.shadowRadius = 0.5f;
		this.addRenderLayer(new MagmaticChampionLayer(this));
	}

	@Override
	public RenderType getRenderType(MagmaticChampionEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}

	@Override
	public void preRender(PoseStack poseStack, MagmaticChampionEntity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red,
			float green, float blue, float alpha) {
		float scale = 1f;
		this.scaleHeight = scale;
		this.scaleWidth = scale;
		super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	protected float getDeathMaxRotation(MagmaticChampionEntity entityLivingBaseIn) {
		return 0.0F;
	}
}
