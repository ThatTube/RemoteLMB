
package net.mcreator.morebosses.item;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.network.chat.Component;

import java.util.List;

public class OldSteelSmithingUpgradeItem extends Item {
	public OldSteelSmithingUpgradeItem() {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON));
	}

	@Override
	public boolean hasCraftingRemainingItem() {
		return true;
	}

	@Override
	public ItemStack getCraftingRemainingItem(ItemStack itemstack) {
		return new ItemStack(this);
	}

	@Override
	public void appendHoverText(ItemStack itemstack, Level level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(itemstack, level, list, flag);
		list.add(Component.translatable("item.morebosses.old_steel_smithing_upgrade.description_0"));
		list.add(Component.translatable("item.morebosses.old_steel_smithing_upgrade.description_1"));
		list.add(Component.translatable("item.morebosses.old_steel_smithing_upgrade.description_2"));
		list.add(Component.translatable("item.morebosses.old_steel_smithing_upgrade.description_3"));
		list.add(Component.translatable("item.morebosses.old_steel_smithing_upgrade.description_4"));
	}
}
