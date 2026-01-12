
package net.mcreator.morebosses.client.renderer;

import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.cache.object.BakedGeoModel;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

import net.mcreator.morebosses.entity.model.CopperMonstrosityModel;
import net.mcreator.morebosses.entity.CopperMonstrosityEntity;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;

public class CopperMonstrosityRenderer extends GeoEntityRenderer<CopperMonstrosityEntity> {
	public CopperMonstrosityRenderer(EntityRendererProvider.Context renderManager) {
		super(renderManager, new CopperMonstrosityModel());
		this.shadowRadius = 0.5f;
	}

	@Override
	public RenderType getRenderType(CopperMonstrosityEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
		return RenderType.entityTranslucent(getTextureLocation(animatable));
	}

	@Override
	public void preRender(PoseStack poseStack, CopperMonstrosityEntity entity, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red,
			float green, float blue, float alpha) {
		float scale = 1.5f;
		this.scaleHeight = scale;
		this.scaleWidth = scale;
		super.preRender(poseStack, entity, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	protected float getDeathMaxRotation(CopperMonstrosityEntity entityLivingBaseIn) {
		return 0.0F;
	}
}
