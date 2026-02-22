package net.mcreator.morebosses.procedures;

import net.minecraft.world.entity.Entity;

public class LaserBeamEntityVisualScaleProcedure {
	public static double execute(Entity entity) {
		if (entity == null)
			return 0;
		if (entity.tickCount <= 1) {
			return 0;
		}
		return 1;
	}
}
