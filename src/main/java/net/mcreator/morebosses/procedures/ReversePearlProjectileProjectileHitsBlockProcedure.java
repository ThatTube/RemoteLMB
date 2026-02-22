package net.mcreator.morebosses.procedures;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.Entity;

public class ReversePearlProjectileProjectileHitsBlockProcedure {
	public static void execute(double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		entity.setDeltaMovement(new Vec3(x, y, z));
	}
}
