
package net.mcreator.morebosses.item;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;

public class IgnitionKeyItem extends Item {
	public IgnitionKeyItem() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.RARE));
	}
}
