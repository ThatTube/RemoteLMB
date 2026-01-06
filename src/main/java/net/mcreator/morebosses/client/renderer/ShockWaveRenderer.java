
package net.mcreator.morebosses.client.renderer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.Minecraft;

import net.mcreator.morebosses.entity.ShockWaveEntity;
import net.mcreator.morebosses.client.model.Modelshockwave;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;

public class ShockWaveRenderer extends MobRenderer<ShockWaveEntity, Modelshockwave<ShockWaveEntity>> {
	public ShockWaveRenderer(EntityRendererProvider.Context context) {
		super(context, new Modelshockwave<ShockWaveEntity>(context.bakeLayer(Modelshockwave.LAYER_LOCATION)), 0.5f);
		this.addLayer(new RenderLayer<ShockWaveEntity, Modelshockwave<ShockWaveEntity>>(this) {
			final ResourceLocation LAYER_TEXTURE = new ResourceLocation("morebosses:textures/entities/wave.png");

			@Override
			public void render(PoseStack poseStack, MultiBufferSource bufferSource, int light, ShockWaveEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
				VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.eyes(LAYER_TEXTURE));
				EntityModel model = new Modelshockwave(Minecraft.getInstance().getEntityModels().bakeLayer(Modelshockwave.LAYER_LOCATION));
				this.getParentModel().copyPropertiesTo(model);
				model.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
				model.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
				model.renderToBuffer(poseStack, vertexConsumer, light, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
			}
		});
	}

	@Override
	public ResourceLocation getTextureLocation(ShockWaveEntity entity) {
		return new ResourceLocation("morebosses:textures/entities/wave.png");
	}
}
