
package net.mcreator.morebosses.potion;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;

import net.mcreator.morebosses.procedures.ShrimpedOnEffectActiveTickProcedure;

public class ShrimpedMobEffect extends MobEffect {
	public ShrimpedMobEffect() {
		super(MobEffectCategory.BENEFICIAL, -41984);
	}

	@Override
	public void applyEffectTick(LivingEntity entity, int amplifier) {
		ShrimpedOnEffectActiveTickProcedure.execute(entity.level(), entity.getX(), entity.getY(), entity.getZ(), entity);
	}

	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		return true;
	}
}
