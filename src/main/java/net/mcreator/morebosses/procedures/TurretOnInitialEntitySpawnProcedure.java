package net.mcreator.morebosses.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.Entity;

import net.mcreator.morebosses.init.MorebossesModGameRules;
import net.mcreator.morebosses.MorebossesMod;

public class TurretOnInitialEntitySpawnProcedure {
	public static void execute(LevelAccessor world, Entity entity) {
		if (entity == null)
			return;
		MorebossesMod.queueServerWork(2200, () -> {
			if (!(entity instanceof TamableAnimal _tamEnt ? _tamEnt.isTame() : false) || !(world.getLevelData().getGameRules().getBoolean(MorebossesModGameRules.PERMA_TURRETS) == true)) {
				if (!entity.level().isClientSide())
					entity.discard();
			}
		});
	}
}
