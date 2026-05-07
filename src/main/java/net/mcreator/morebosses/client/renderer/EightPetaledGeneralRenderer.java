
package net.mcreator.morebosses.client.renderer;

import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.cache.object.BakedGeoModel;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

import net.mcreator.morebosses.entity.model.EightPetaledGeneralModel;
import net.mcreator.morebosses.entity.EightPetaledGeneralEntity;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;

public class EightPetaledGeneralRenderer extends GeoEntityRenderer<EightPetaledGeneralEntity> {
	public EightPetaledGeneralRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new EightPetaledGeneralModel());
		this.shadowRadius = 0.5f;
	}

	@Override
	public RenderType getRenderType(EightPetaledGeneralEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}

	@Override
	public void preRender(PoseStack poseStack, EightPetaledGeneralEntity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red,
			float green, float blue, float alpha) {
		float scale = 1f;
		this.scaleHeight = scale;
		this.scaleWidth = scale;
		super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	protected float getDeathMaxRotation(EightPetaledGeneralEntity entityLivingBaseIn) {
		return 0.0F;
	}
}
