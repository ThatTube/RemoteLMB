
package net.mcreator.morebosses.item;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;
import net.minecraft.world.food.FoodProperties;

public class StrokeCakeItem extends Item {
	public StrokeCakeItem() {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON).food((new FoodProperties.Builder()).nutrition(6).saturationMod(2f).build()));
	}
}
