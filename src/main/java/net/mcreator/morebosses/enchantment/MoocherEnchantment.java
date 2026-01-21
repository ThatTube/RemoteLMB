
package net.mcreator.morebosses.enchantment;

import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.EquipmentSlot;

import net.mcreator.morebosses.init.MorebossesModItems;
import net.mcreator.morebosses.init.MorebossesModEnchantments;

import java.util.List;

public class MoocherEnchantment extends Enchantment {
	private static final EnchantmentCategory ENCHANTMENT_CATEGORY = EnchantmentCategory.create("morebosses_moocher", item -> Ingredient.of(new ItemStack(MorebossesModItems.MACABRE_SCYTHE.get())).test(new ItemStack(item)));

	public MoocherEnchantment() {
		super(Enchantment.Rarity.COMMON, ENCHANTMENT_CATEGORY, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
	}

	@Override
	public int getMinCost(int level) {
		return 1 + level * 10;
	}

	@Override
	public int getMaxCost(int level) {
		return 6 + level * 10;
	}

	@Override
	protected boolean checkCompatibility(Enchantment enchantment) {
		return super.checkCompatibility(enchantment) && !List.of(MorebossesModEnchantments.SHOW_OFF.get()).contains(enchantment);
	}
}
