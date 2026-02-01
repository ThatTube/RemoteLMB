package net.mcreator.morebosses.entity.model;

import software.bernie.geckolib.model.data.EntityModelData;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.constant.DataTickets;

import net.minecraft.util.Mth;
import net.minecraft.resources.ResourceLocation;

import net.mcreator.morebosses.entity.PiglinBoxerEntity;

public class PiglinBoxerModel extends GeoModel<PiglinBoxerEntity> {
	@Override
	public ResourceLocation getAnimationResource(PiglinBoxerEntity entity) {
		return new ResourceLocation("morebosses", "animations/piglin_boxer.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(PiglinBoxerEntity entity) {
		return new ResourceLocation("morebosses", "geo/piglin_boxer.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(PiglinBoxerEntity entity) {
		return new ResourceLocation("morebosses", "textures/entities/" + entity.getTexture() + ".png");
	}

	@Override
	public void setCustomAnimations(PiglinBoxerEntity animatable, long instanceId, AnimationState animationState) {
		CoreGeoBone head = getAnimationProcessor().getBone("head");
		if (head != null) {
			EntityModelData entityData = (EntityModelData) animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}

	}
}
