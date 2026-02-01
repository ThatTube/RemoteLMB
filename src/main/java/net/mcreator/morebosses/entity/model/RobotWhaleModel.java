package net.mcreator.morebosses.entity.model;

import software.bernie.geckolib.model.data.EntityModelData;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.constant.DataTickets;

import net.minecraft.util.Mth;
import net.minecraft.resources.ResourceLocation;

import net.mcreator.morebosses.entity.RobotWhaleEntity;

public class RobotWhaleModel extends GeoModel<RobotWhaleEntity> {
	@Override
	public ResourceLocation getAnimationResource(RobotWhaleEntity entity) {
		return new ResourceLocation("morebosses", "animations/robot_whale.animation.json");
	}

	@Override
	public ResourceLocation getModelResource(RobotWhaleEntity entity) {
		return new ResourceLocation("morebosses", "geo/robot_whale.geo.json");
	}

	@Override
	public ResourceLocation getTextureResource(RobotWhaleEntity entity) {
		return new ResourceLocation("morebosses", "textures/entities/" + entity.getTexture() + ".png");
	}

	@Override
	public void setCustomAnimations(RobotWhaleEntity animatable, long instanceId, AnimationState animationState) {
		CoreGeoBone head = getAnimationProcessor().getBone("head");
		if (head != null) {
			EntityModelData entityData = (EntityModelData) animationState.getData(DataTickets.ENTITY_MODEL_DATA);
			head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
			head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
		}

	}
}
