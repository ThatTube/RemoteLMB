
package net.mcreator.morebosses.client.renderer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

import net.mcreator.morebosses.entity.ShrimpEntity;
import net.mcreator.morebosses.client.model.ModelShrimp;

import com.mojang.blaze3d.vertex.PoseStack;

public class ShrimpRenderer extends MobRenderer<ShrimpEntity, ModelShrimp<ShrimpEntity>> {
	public ShrimpRenderer(EntityRendererProvider.Context context) {
		super(context, new ModelShrimp<ShrimpEntity>(context.bakeLayer(ModelShrimp.LAYER_LOCATION)), 0.5f);
	}

	@Override
	protected void scale(ShrimpEntity entity, PoseStack poseStack, float f) {
		poseStack.scale(0.35f, 0.35f, 0.35f);
	}

	@Override
	public ResourceLocation getTextureLocation(ShrimpEntity entity) {
		return new ResourceLocation("morebosses:textures/entities/shrimp.png");
	}
}
