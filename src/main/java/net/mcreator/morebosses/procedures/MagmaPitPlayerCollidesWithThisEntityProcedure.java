package net.mcreator.morebosses.procedures;

import net.minecraft.world.entity.Entity;

public class MagmaPitPlayerCollidesWithThisEntityProcedure {
	public static void execute(Entity sourceentity) {
		if (sourceentity == null)
			return;
		sourceentity.setSecondsOnFire(15);
	}
}
