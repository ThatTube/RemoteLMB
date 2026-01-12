package net.mcreator.morebosses.procedures;

import net.minecraft.world.entity.Entity;

import net.mcreator.morebosses.entity.DryBonesEntity;

public class DryBonesLolProcedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		if (entity instanceof DryBonesEntity) {
			((DryBonesEntity) entity).setAnimation("lol");
		}
	}
}
