package net.mcreator.morebosses.entity.model;

import software.bernie.geckolib.model.data.EntityModelData;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.constant.DataTickets;

import net.minecraft.util.Mth;
import net.minecraft.resources.ResourceLocation;

import net.mcreator.morebosses.entity.EnderSnakeEntity;

public class EnderSnakeModel extends GeoModel<EnderSnakeEntity> {
	@Override
	public ResourceLocation getAnimationResource(EnderSnakeEntity entity) {
		return new ResourceLocation("morebosses", "animations/esnake.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(EnderSnakeEntity entity) {
		return new ResourceLocation("morebosses", "geo/esnake.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(EnderSnakeEntity entity) {
		return new ResourceLocation("morebosses", "textures/entities/" + entity.getTexture() + ".png");
	}

	@Override
	public void setCustomAnimations(EnderSnakeEntity animatable, long instanceId, AnimationState animationState) {
		CoreGeoBone head = getAnimationProcessor().getBone("realhead");
		if (head != null) {
			EntityModelData entityData = (EntityModelData) animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}

	}
}
