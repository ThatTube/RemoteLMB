
package net.mcreator.morebosses.potion;

import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;

public class WatchedMobEffect extends MobEffect {
	public WatchedMobEffect() {
		super(MobEffectCategory.HARMFUL, -13145554);
		this.addAttributeModifier(Attributes.LUCK, "08f06eb4-9bfc-3b4f-818b-5ef050e42798", -2, AttributeModifier.Operation.ADDITION);
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
