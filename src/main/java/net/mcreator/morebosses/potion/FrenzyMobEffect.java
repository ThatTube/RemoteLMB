
package net.mcreator.morebosses.potion;

import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;

public class FrenzyMobEffect extends MobEffect {
	public FrenzyMobEffect() {
		super(MobEffectCategory.BENEFICIAL, -1);
		this.addAttributeModifier(Attributes.ATTACK_SPEED, "124492ae-1bab-36bc-8d0e-e02f1d892432", 0.5, AttributeModifier.Operation.ADDITION);
		this.addAttributeModifier(Attributes.ATTACK_KNOCKBACK, "3e597fc9-3fc8-333c-b30b-083b64b871f9", 0.25, AttributeModifier.Operation.ADDITION);
		this.addAttributeModifier(Attributes.ATTACK_DAMAGE, "26408571-adf5-3857-9be6-656c1bddb79a", 0.5, AttributeModifier.Operation.ADDITION);
	}

	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		return true;
	}
}
