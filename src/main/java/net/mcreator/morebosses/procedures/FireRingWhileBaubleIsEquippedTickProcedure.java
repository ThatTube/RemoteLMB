package net.mcreator.morebosses.procedures;

import net.minecraft.world.entity.Entity;

public class FireRingWhileBaubleIsEquippedTickProcedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		entity.clearFire();
	}
}
