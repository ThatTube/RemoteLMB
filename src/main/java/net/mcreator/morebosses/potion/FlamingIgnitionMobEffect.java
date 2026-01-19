
package net.mcreator.morebosses.potion;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;

public class FlamingIgnitionMobEffect extends MobEffect {
	public FlamingIgnitionMobEffect() {
		super(MobEffectCategory.BENEFICIAL, -31232);
	}

	@Override
	public boolean isInstantenous() {
		return true;
	}

	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		return true;
	}
}
