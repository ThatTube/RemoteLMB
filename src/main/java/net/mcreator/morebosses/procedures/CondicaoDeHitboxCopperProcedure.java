package net.mcreator.morebosses.procedures;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Entity;

public class CondicaoDeHitboxCopperProcedure {
	public static boolean execute(Entity entity) {
		if (entity == null)
			return false;
		return ((entity instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null) != null ? entity.distanceTo((entity instanceof Mob _mobEnt ? (Entity) _mobEnt.getTarget() : null)) : -1) > 5;
	}
}
