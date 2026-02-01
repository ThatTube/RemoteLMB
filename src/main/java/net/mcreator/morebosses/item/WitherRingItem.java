
package net.mcreator.morebosses.item;

import top.theillusivec4.curios.api.type.capability.ICurioItem;
import top.theillusivec4.curios.api.SlotContext;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;

import net.mcreator.morebosses.procedures.WitherRingWhileBaubleIsEquippedTickProcedure;

public class WitherRingItem extends Item implements ICurioItem {
	public WitherRingItem() {
		super(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON));
	}

	@Override
	public void curioTick(SlotContext slotContext, ItemStack stack) {
		WitherRingWhileBaubleIsEquippedTickProcedure.execute(slotContext.entity());
	}
}
