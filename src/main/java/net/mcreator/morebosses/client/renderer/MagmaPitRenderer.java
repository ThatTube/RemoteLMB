
package net.mcreator.morebosses.client.renderer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource;

import net.mcreator.morebosses.entity.MagmaPitEntity;
import net.mcreator.morebosses.client.model.Modelmagma_pit;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack;

public class MagmaPitRenderer extends MobRenderer<MagmaPitEntity, Modelmagma_pit<MagmaPitEntity>> {
	public MagmaPitRenderer(EntityRendererProvider.Context context) {
		super(context, new Modelmagma_pit<MagmaPitEntity>(context.bakeLayer(Modelmagma_pit.LAYER_LOCATION)), 0f);
		this.addLayer(new RenderLayer<MagmaPitEntity, Modelmagma_pit<MagmaPitEntity>>(this) {
			final ResourceLocation LAYER_TEXTURE = new ResourceLocation("morebosses:textures/entities/magma_pit.png");

			@Override
			public void render(PoseStack poseStack, MultiBufferSource bufferSource, int light, MagmaPitEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
				VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.eyes(LAYER_TEXTURE));
				this.getParentModel().renderToBuffer(poseStack, vertexConsumer, light, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
			}
		});
	}

	@Override
	protected void scale(MagmaPitEntity entity, PoseStack poseStack, float f) {
		poseStack.scale(1.01f, 1.01f, 1.01f);
	}

	@Override
	public ResourceLocation getTextureLocation(MagmaPitEntity entity) {
		return new ResourceLocation("morebosses:textures/entities/magma_pit.png");
	}
}
