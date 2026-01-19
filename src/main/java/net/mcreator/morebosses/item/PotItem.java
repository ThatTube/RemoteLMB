
package net.mcreator.morebosses.item;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;

public class PotItem extends Item {
	public PotItem() {
		super(new Item.Properties().stacksTo(16).rarity(Rarity.COMMON));
	}
}
