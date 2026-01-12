
package net.mcreator.morebosses.potion;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;

import java.util.List;
import java.util.ArrayList;

public class PanicMobEffect extends MobEffect {
	public PanicMobEffect() {
		super(MobEffectCategory.HARMFUL, -1);
		this.addAttributeModifier(Attributes.ATTACK_DAMAGE, "9887b874-83f7-3c8f-bfc5-154160fbbae6", -7, AttributeModifier.Operation.ADDITION);
		this.addAttributeModifier(Attributes.ATTACK_SPEED, "59e2e77e-53ff-3a02-b48a-10951e2a569f", -1, AttributeModifier.Operation.ADDITION);
		this.addAttributeModifier(Attributes.ATTACK_KNOCKBACK, "7f0d0060-5152-36e5-8eee-2916df1c2462", -2, AttributeModifier.Operation.ADDITION);
		this.addAttributeModifier(Attributes.MOVEMENT_SPEED, "c427eb1e-538e-3bc2-a7af-59071c99a807", -3, AttributeModifier.Operation.ADDITION);
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
