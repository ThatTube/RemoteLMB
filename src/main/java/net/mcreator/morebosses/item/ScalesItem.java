
package net.mcreator.morebosses.item;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;

public class ScalesItem extends Item {
	public ScalesItem() {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON));
	}
}
