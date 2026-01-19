package net.mcreator.morebosses.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;

import net.mcreator.morebosses.MorebossesMod;

public class EngineerEntityIsHurtProcedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		entity.setSprinting(true);
		MorebossesMod.queueServerWork(40, () -> {
			entity.setSprinting(false);
		});
	}
}
