package net.mcreator.morebosses.procedures;

import top.theillusivec4.curios.api.CuriosApi;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffects;

import net.mcreator.morebosses.init.MorebossesModItems;

public class WitherRingWhileBaubleIsEquippedTickProcedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		if (entity instanceof LivingEntity lv ? CuriosApi.getCuriosHelper().findEquippedCurio(MorebossesModItems.WITHER_RING.get(), lv).isPresent() : false && entity instanceof LivingEntity _livEnt1 && _livEnt1.hasEffect(MobEffects.WITHER)) {
			if (entity instanceof LivingEntity _entity)
				_entity.removeEffect(MobEffects.WITHER);
		}
	}
}
