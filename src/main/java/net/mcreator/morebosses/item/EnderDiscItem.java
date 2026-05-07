
package net.mcreator.morebosses.item;

import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.item.RecordItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;
import net.minecraft.resources.ResourceLocation;

public class EnderDiscItem extends RecordItem {
	public EnderDiscItem() {
		super(0, () -> ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("morebosses:endertp")), new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON), 5180);
	}
}
