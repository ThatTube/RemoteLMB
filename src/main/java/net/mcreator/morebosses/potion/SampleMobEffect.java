
package net.mcreator.morebosses.potion;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;

import java.util.List;
import java.util.ArrayList;

public class SampleMobEffect extends MobEffect {
	public SampleMobEffect() {
		super(MobEffectCategory.NEUTRAL, -10066330);
		this.addAttributeModifier(Attributes.ARMOR, "c3fdac46-054d-3822-9ef8-6fe675b7ec19", -2, AttributeModifier.Operation.ADDITION);
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
