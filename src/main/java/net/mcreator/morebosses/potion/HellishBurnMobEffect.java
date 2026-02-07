
package net.mcreator.morebosses.potion;

import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;

import net.mcreator.morebosses.procedures.HellishBurnOnEffectActiveTickProcedure;

public class HellishBurnMobEffect extends MobEffect {
	public HellishBurnMobEffect() {
		super(MobEffectCategory.HARMFUL, -3407872);
		this.addAttributeModifier(Attributes.ARMOR_TOUGHNESS, "8d8cb9df-d8b2-35e2-bb4e-c88a141964aa", -1, AttributeModifier.Operation.ADDITION);
	}

	@Override
	public void applyEffectTick(LivingEntity entity, int amplifier) {
		HellishBurnOnEffectActiveTickProcedure.execute(entity.level(), entity);
	}

	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		return true;
	}
}
