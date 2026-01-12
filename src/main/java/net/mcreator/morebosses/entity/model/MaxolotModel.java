package net.mcreator.morebosses.entity.model;

import software.bernie.geckolib.model.data.EntityModelData;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.constant.DataTickets;

import net.minecraft.util.Mth;
import net.minecraft.resources.ResourceLocation;

import net.mcreator.morebosses.entity.MaxolotEntity;

public class MaxolotModel extends GeoModel<MaxolotEntity> {
	@Override
	public ResourceLocation getAnimationResource(MaxolotEntity entity) {
		return new ResourceLocation("morebosses", "animations/maxolotl.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(MaxolotEntity entity) {
		return new ResourceLocation("morebosses", "geo/maxolotl.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(MaxolotEntity entity) {
		return new ResourceLocation("morebosses", "textures/entities/" + entity.getTexture() + ".png");
	}

	@Override
	public void setCustomAnimations(MaxolotEntity animatable, long instanceId, AnimationState animationState) {
		CoreGeoBone head = getAnimationProcessor().getBone("head");
		if (head != null) {
			EntityModelData entityData = (EntityModelData) animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}

	}
}
