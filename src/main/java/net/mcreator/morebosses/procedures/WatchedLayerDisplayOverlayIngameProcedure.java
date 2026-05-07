package net.mcreator.morebosses.procedures;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;

import net.mcreator.morebosses.init.MorebossesModMobEffects;

public class WatchedLayerDisplayOverlayIngameProcedure {
	public static boolean execute(Entity entity) {
		if (entity == null)
			return false;
		if (entity instanceof LivingEntity _livEnt0 && _livEnt0.hasEffect(MorebossesModMobEffects.WATCHED.get())) {
			return true;
		}
		return false;
	}
}
