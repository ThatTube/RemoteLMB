package net.mcreator.morebosses.procedures;

import net.minecraft.world.entity.Entity;

public class DisguisedOnEffectActiveTickProcedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		entity.setInvisible(true);
	}
}
