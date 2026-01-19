
package net.mcreator.morebosses.potion;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;

import java.util.List;
import java.util.ArrayList;

public class EngineBoostMobEffect extends MobEffect {
	public EngineBoostMobEffect() {
		super(MobEffectCategory.NEUTRAL, -9377793);
		this.addAttributeModifier(Attributes.ATTACK_SPEED, "ff03eaf7-edae-3e5c-86ca-f7d034946acf", 1, AttributeModifier.Operation.ADDITION);
		this.addAttributeModifier(Attributes.ATTACK_KNOCKBACK, "464654cf-b54e-3427-b08d-35909b8748ff", 1, AttributeModifier.Operation.ADDITION);
		this.addAttributeModifier(Attributes.MOVEMENT_SPEED, "c961dd14-39c7-3756-8448-8946fe41840f", 1.5, AttributeModifier.Operation.MULTIPLY_BASE);
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
