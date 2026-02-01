
package net.mcreator.morebosses.item;

import top.theillusivec4.curios.api.type.capability.ICurioItem;
import top.theillusivec4.curios.api.SlotContext;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;

import net.mcreator.morebosses.procedures.PoisonRingWhileBaubleIsEquippedTickProcedure;

public class PoisonRingItem extends Item implements ICurioItem {
	public PoisonRingItem() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON));
	}

	@Override
	public void curioTick(SlotContext slotContext, ItemStack stack) {
		PoisonRingWhileBaubleIsEquippedTickProcedure.execute(slotContext.entity());
	}
}
