
package net.mcreator.morebosses.client.renderer;

import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.cache.object.BakedGeoModel;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

import net.mcreator.morebosses.entity.model.DryBonesModel;
import net.mcreator.morebosses.entity.layer.DryBonesLayer;
import net.mcreator.morebosses.entity.DryBonesEntity;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;

public class DryBonesRenderer extends GeoEntityRenderer<DryBonesEntity> {
	public DryBonesRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new DryBonesModel());
		this.shadowRadius = 0.5f;
		this.addRenderLayer(new DryBonesLayer(this));
	}

	@Override
	public RenderType getRenderType(DryBonesEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}

	@Override
	public void preRender(PoseStack poseStack, DryBonesEntity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green,
			float blue, float alpha) {
		float scale = 1.5f;
		this.scaleHeight = scale;
		this.scaleWidth = scale;
		super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	protected float getDeathMaxRotation(DryBonesEntity entityLivingBaseIn) {
		return 0.0F;
	}
}
