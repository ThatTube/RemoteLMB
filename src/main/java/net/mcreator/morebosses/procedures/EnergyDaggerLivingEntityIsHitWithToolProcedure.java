package net.mcreator.morebosses.procedures;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffectInstance;

import net.mcreator.morebosses.init.MorebossesModMobEffects;

public class EnergyDaggerLivingEntityIsHitWithToolProcedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
			_entity.addEffect(new MobEffectInstance(MorebossesModMobEffects.WATCHED.get(), 60, 0, false, false));
	}
}
