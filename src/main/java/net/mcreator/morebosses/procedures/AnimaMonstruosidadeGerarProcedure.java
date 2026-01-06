package net.mcreator.morebosses.procedures;

import net.minecraft.world.entity.Entity;

import net.mcreator.morebosses.entity.CopperMonstrosityEntity;

public class AnimaMonstruosidadeGerarProcedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		if (entity instanceof CopperMonstrosityEntity) {
			((CopperMonstrosityEntity) entity).setAnimation("spawn");
		}
	}
}
