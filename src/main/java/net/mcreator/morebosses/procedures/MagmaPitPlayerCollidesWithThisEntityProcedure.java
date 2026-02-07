package net.mcreator.morebosses.procedures;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffectInstance;

import net.mcreator.morebosses.init.MorebossesModMobEffects;

public class MagmaPitPlayerCollidesWithThisEntityProcedure {
	public static void execute(Entity sourceentity) {
		if (sourceentity == null)
			return;
		sourceentity.setSecondsOnFire(15);
		if (sourceentity instanceof LivingEntity _entity && !_entity.level().isClientSide())
			_entity.addEffect(new MobEffectInstance(MorebossesModMobEffects.HELLISH_BURN.get(), 60, 0));
	}
}
