package net.mcreator.morebosses.procedures;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;

import net.mcreator.morebosses.entity.MinilotlEntity;

public class MinilotlOnEntityTickUpdateProcedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		if (entity.getPersistentData().getBoolean("sit") == true) {
			if (entity instanceof MinilotlEntity) {
				((MinilotlEntity) entity).setAnimation("sit");
			}
			if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
				_entity.forceAddEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 255, false, false), entity);
		}
	}
}
