
package net.mcreator.morebosses.item;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;

public class RawMagmaCreamItem extends Item {
	public RawMagmaCreamItem() {
		super(new Item.Properties().stacksTo(64).fireResistant().rarity(Rarity.UNCOMMON));
	}
}
