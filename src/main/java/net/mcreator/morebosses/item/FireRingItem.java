
package net.mcreator.morebosses.item;

import top.theillusivec4.curios.api.type.capability.ICurioItem;
import top.theillusivec4.curios.api.SlotContext;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.Entity;

import net.mcreator.morebosses.procedures.FireRingWhileBaubleIsEquippedTickProcedure;

public class FireRingItem extends Item implements ICurioItem {
	public FireRingItem() {
		super(new Item.Properties().stacksTo(1).fireResistant().rarity(Rarity.UNCOMMON));
	}

	@Override
	public void inventoryTick(ItemStack itemstack, Level world, Entity entity, int slot, boolean selected) {
		super.inventoryTick(itemstack, world, entity, slot, selected);
		FireRingWhileBaubleIsEquippedTickProcedure.execute(entity);
	}

	@Override
	public void curioTick(SlotContext slotContext, ItemStack stack) {
		FireRingWhileBaubleIsEquippedTickProcedure.execute(slotContext.entity());
	}
}
