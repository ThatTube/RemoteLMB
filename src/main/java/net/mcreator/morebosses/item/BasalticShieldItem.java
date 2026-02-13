
package net.mcreator.morebosses.item;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;

import net.mcreator.morebosses.init.MorebossesModItems;

public class BasalticShieldItem extends ShieldItem {
	public BasalticShieldItem() {
		super(new Item.Properties().durability(2672));
	}

	@Override
	public boolean isValidRepairItem(ItemStack itemstack, ItemStack repairitem) {
		return Ingredient.of(new ItemStack(MorebossesModItems.BASALTIC_SHARD.get()), new ItemStack(Items.NETHERITE_INGOT)).test(repairitem);
	}
}
