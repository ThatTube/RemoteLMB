
package net.mcreator.morebosses.item;

import top.theillusivec4.curios.api.type.capability.ICurioItem;
import top.theillusivec4.curios.api.SlotContext;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;

import net.mcreator.morebosses.procedures.CuirlassBracerWhileBaubleIsEquippedTickProcedure;

public class CuirlassBracerItem extends Item implements ICurioItem {
	public CuirlassBracerItem() {
		super(new Item.Properties().stacksTo(1).fireResistant().rarity(Rarity.RARE));
	}

	@Override
	public void curioTick(SlotContext slotContext, ItemStack stack) {
		CuirlassBracerWhileBaubleIsEquippedTickProcedure.execute();
	}
}
