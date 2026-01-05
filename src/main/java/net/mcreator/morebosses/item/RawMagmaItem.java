
package net.mcreator.morebosses.item;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.BucketItem;

import net.mcreator.morebosses.init.MorebossesModFluids;

public class RawMagmaItem extends BucketItem {
	public RawMagmaItem() {
		super(MorebossesModFluids.RAW_MAGMA, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).rarity(Rarity.COMMON));
	}
}
