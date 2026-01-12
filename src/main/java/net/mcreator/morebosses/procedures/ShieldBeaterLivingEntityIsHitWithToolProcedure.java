
package net.mcreator.morebosses.procedures;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.tags.ItemTags;
import net.minecraft.resources.ResourceLocation;

public class ShieldBeaterLivingEntityIsHitWithToolProcedure {
	public static void execute(Entity entity, Entity sourceentity) {
		if (entity == null || sourceentity == null)
			return;
		if (entity instanceof LivingEntity target && target.isBlocking()) {
			if ((target.getMainHandItem()).is(ItemTags.create(new ResourceLocation("minecraft:shields")))) {
				if (entity instanceof Player player)
					player.getCooldowns().addCooldown(target.getMainHandItem().getItem(), 250);
			}
			if ((target.getOffhandItem()).is(ItemTags.create(new ResourceLocation("minecraft:shields")))) {
				if (entity instanceof Player player)
					player.getCooldowns().addCooldown(target.getOffhandItem().getItem(), 250);
			}
		}
	}
}
