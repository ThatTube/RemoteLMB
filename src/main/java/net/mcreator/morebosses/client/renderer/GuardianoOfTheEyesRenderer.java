
package net.mcreator.morebosses.client.renderer;

import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.cache.object.BakedGeoModel;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

import net.mcreator.morebosses.entity.model.GuardianoOfTheEyesModel;
import net.mcreator.morebosses.entity.layer.GuardianoOfTheEyesLayer;
import net.mcreator.morebosses.entity.GuardianoOfTheEyesEntity;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;

public class GuardianoOfTheEyesRenderer extends GeoEntityRenderer<GuardianoOfTheEyesEntity> {
	public GuardianoOfTheEyesRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new GuardianoOfTheEyesModel());
		this.shadowRadius = 1f;
		this.addRenderLayer(new GuardianoOfTheEyesLayer(this));
	}

	@Override
	public RenderType getRenderType(GuardianoOfTheEyesEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}

	@Override
	public void preRender(PoseStack poseStack, GuardianoOfTheEyesEntity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red,
			float green, float blue, float alpha) {
		float scale = 1f;
		this.scaleHeight = scale;
		this.scaleWidth = scale;
		super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	protected float getDeathMaxRotation(GuardianoOfTheEyesEntity entityLivingBaseIn) {
		return 0.0F;
	}
}
