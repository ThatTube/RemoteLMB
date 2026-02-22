
package net.mcreator.morebosses.client.renderer;

import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.cache.object.BakedGeoModel;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

import net.mcreator.morebosses.entity.model.EyeSentinelModel;
import net.mcreator.morebosses.entity.layer.EyeSentinelLayer;
import net.mcreator.morebosses.entity.EyeSentinelEntity;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;

public class EyeSentinelRenderer extends GeoEntityRenderer<EyeSentinelEntity> {
	public EyeSentinelRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new EyeSentinelModel());
		this.shadowRadius = 0.5f;
		this.addRenderLayer(new EyeSentinelLayer(this));
	}

	@Override
	public RenderType getRenderType(EyeSentinelEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}

	@Override
	public void preRender(PoseStack poseStack, EyeSentinelEntity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green,
			float blue, float alpha) {
		float scale = 1f;
		this.scaleHeight = scale;
		this.scaleWidth = scale;
		super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	protected float getDeathMaxRotation(EyeSentinelEntity entityLivingBaseIn) {
		return 0.0F;
	}
}
