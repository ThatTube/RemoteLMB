
package net.mcreator.morebosses.potion;

import net.minecraftforge.common.ForgeMod;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;

import java.util.List;
import java.util.ArrayList;

public class StickyMobEffect extends MobEffect {
	public StickyMobEffect() {
		super(MobEffectCategory.HARMFUL, -16777216);
		this.addAttributeModifier(Attributes.ATTACK_SPEED, "4fad2721-31dc-3348-b9dc-654ecbe71698", -1, AttributeModifier.Operation.ADDITION);
		this.addAttributeModifier(Attributes.MOVEMENT_SPEED, "875545ed-7ad4-3ca6-8a5f-3fe9cc6f4124", -0.5, AttributeModifier.Operation.ADDITION);
		this.addAttributeModifier(ForgeMod.ENTITY_GRAVITY.get(), "e7d8afcf-20f7-3aa5-b1e5-d6c1ccffa5db", 0.5, AttributeModifier.Operation.ADDITION);
	}

	@Override
	public boolean isInstantenous() {
		return true;
	}

	@Override
	public List<ItemStack> getCurativeItems() {
		ArrayList<ItemStack> cures = new ArrayList<ItemStack>();
		return cures;
	}

	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		return true;
	}
}
