package net.mcreator.morebosses.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.Entity;

public class SoulDionaeaOnEntityTickUpdateProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		if ((entity.getPersistentData().getString("State")).equals("Idle")) {
			PlantAttackDetecitonProcedure.execute(world, x, y, z, entity);
		}
		if ((entity.getPersistentData().getString("State")).equals("Attack")) {
			PlantaAtaqueProcedure.execute(world, x, y, z, entity);
		}
	}
}
