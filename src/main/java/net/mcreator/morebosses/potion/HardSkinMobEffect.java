
package net.mcreator.morebosses.potion;

import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;

public class HardSkinMobEffect extends MobEffect {
	public HardSkinMobEffect() {
		super(MobEffectCategory.BENEFICIAL, -13948335);
		this.addAttributeModifier(Attributes.ARMOR_TOUGHNESS, "8b3a85f0-288d-3fa3-93a2-9b0d9a0161a4", 6, AttributeModifier.Operation.ADDITION);
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
