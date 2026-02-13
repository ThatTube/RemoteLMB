package net.mcreator.morebosses.procedures;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffectInstance;

import net.mcreator.morebosses.init.MorebossesModMobEffects;

public class BrokenSwordLivingEntityIsHitWithToolProcedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		if (Math.random() <= 0.45) {
			if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
				_entity.forceAddEffect(new MobEffectInstance(MorebossesModMobEffects.BLEEDING.get(), 60, 0, false, true), entity);
		}
	}
}
