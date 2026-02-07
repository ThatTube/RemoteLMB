package net.mcreator.morebosses.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;

import net.mcreator.morebosses.init.MorebossesModEntities;
import net.mcreator.morebosses.init.MorebossesModAttributes;

public class ShrimpedOnEffectActiveTickProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		if (Math.random() <= 0.7) {
			if (entity instanceof LivingEntity _livingEntity1 && _livingEntity1.getAttributes().hasAttribute(MorebossesModAttributes.SHRIMP_METTER.get()))
				_livingEntity1.getAttribute(MorebossesModAttributes.SHRIMP_METTER.get())
						.setBaseValue(((entity instanceof LivingEntity _livingEntity0 && _livingEntity0.getAttributes().hasAttribute(MorebossesModAttributes.SHRIMP_METTER.get())
								? _livingEntity0.getAttribute(MorebossesModAttributes.SHRIMP_METTER.get()).getValue()
								: 0) + 1));
		}
		if ((entity instanceof LivingEntity _livingEntity2 && _livingEntity2.getAttributes().hasAttribute(MorebossesModAttributes.SHRIMP_METTER.get()) ? _livingEntity2.getAttribute(MorebossesModAttributes.SHRIMP_METTER.get()).getValue() : 0) >= 64) {
			if (world instanceof ServerLevel _level) {
				Entity entityToSpawn = MorebossesModEntities.SHRIMP.get().spawn(_level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
				if (entityToSpawn != null) {
					entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
				}
			}
			if (entity instanceof LivingEntity _livingEntity4 && _livingEntity4.getAttributes().hasAttribute(MorebossesModAttributes.SHRIMP_METTER.get()))
				_livingEntity4.getAttribute(MorebossesModAttributes.SHRIMP_METTER.get()).setBaseValue(0);
		}
	}
}
