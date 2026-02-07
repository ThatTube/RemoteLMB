
package net.mcreator.morebosses.potion;

import net.minecraftforge.common.ForgeMod;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffect;

import java.util.List;
import java.util.ArrayList;

public class StunMobEffect extends MobEffect {
	public StunMobEffect() {
		super(MobEffectCategory.HARMFUL, -256);
		this.addAttributeModifier(Attributes.ATTACK_SPEED, "11b3d51c-f628-3b73-9ea8-bbb0a9f0ffa8", -45, AttributeModifier.Operation.ADDITION);
		this.addAttributeModifier(Attributes.JUMP_STRENGTH, "4d7ea35f-1317-3123-b844-a49055f469c4", -45, AttributeModifier.Operation.ADDITION);
		this.addAttributeModifier(Attributes.MOVEMENT_SPEED, "34a4cd08-28d1-355d-9e0d-5f806137606e", -45, AttributeModifier.Operation.ADDITION);
		this.addAttributeModifier(ForgeMod.STEP_HEIGHT_ADDITION.get(), "05222e64-964d-3bf0-b401-864e42a489e1", -1, AttributeModifier.Operation.ADDITION);
		this.addAttributeModifier(ForgeMod.ENTITY_GRAVITY.get(), "711b2638-1ec9-38fd-8039-cf3925581e02", 1, AttributeModifier.Operation.ADDITION);
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
