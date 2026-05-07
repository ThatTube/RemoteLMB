
package net.mcreator.morebosses.item;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;

public class IronDaggerItem extends Item {
	public IronDaggerItem() {
		super(new Item.Properties().stacksTo(3).rarity(Rarity.COMMON));
	}
}
