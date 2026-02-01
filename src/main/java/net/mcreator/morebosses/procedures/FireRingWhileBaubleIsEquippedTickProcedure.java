package net.mcreator.morebosses.procedures;

import top.theillusivec4.curios.api.CuriosApi;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;

import net.mcreator.morebosses.init.MorebossesModItems;

public class FireRingWhileBaubleIsEquippedTickProcedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		if (entity instanceof LivingEntity lv ? CuriosApi.getCuriosHelper().findEquippedCurio(MorebossesModItems.FIRE_RING.get(), lv).isPresent() : false) {
			entity.clearFire();
		}
	}
}
