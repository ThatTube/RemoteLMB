package net.mcreator.morebosses.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;

import net.mcreator.morebosses.MorebossesMod;

public class MagmaPitOnInitialEntitySpawnProcedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		MorebossesMod.queueServerWork(160, () -> {
			if (!entity.level().isClientSide())
				entity.discard();
		});
	}
}
