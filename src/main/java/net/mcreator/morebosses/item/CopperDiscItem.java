
package net.mcreator.morebosses.item;

import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.item.RecordItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;
import net.minecraft.resources.ResourceLocation;

public class CopperDiscItem extends RecordItem {
	public CopperDiscItem() {
		super(0, () -> ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("morebosses:copper_placeholder")), new Item.Properties().stacksTo(64).rarity(Rarity.UNCOMMON), 5180);
	}
}
