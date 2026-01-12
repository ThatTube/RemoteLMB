
package net.mcreator.morebosses.item;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;

public class EmptyTankItem extends Item {
	public EmptyTankItem() {
		super(new Item.Properties().stacksTo(64).rarity(Rarity.COMMON));
	}
}
