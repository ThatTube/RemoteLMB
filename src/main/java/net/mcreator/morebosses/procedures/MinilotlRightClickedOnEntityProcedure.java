package net.mcreator.morebosses.procedures;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.chat.Component;

public class MinilotlRightClickedOnEntityProcedure {
	public static void execute(Entity entity, Entity sourceentity) {
		if (entity == null || sourceentity == null)
			return;
		if (entity instanceof TamableAnimal _tamIsTamedBy && sourceentity instanceof LivingEntity _livEnt ? _tamIsTamedBy.isOwnedBy(_livEnt) : false) {
			if (sourceentity.isShiftKeyDown()) {
				if (entity.getPersistentData().getBoolean("sit") == false) {
					entity.getPersistentData().putBoolean("sit", true);
					if (sourceentity instanceof Player _player && !_player.level().isClientSide())
						_player.displayClientMessage(Component.literal("Minilotl is sitting"), true);
				} else if (entity.getPersistentData().getBoolean("sit") == true) {
					entity.getPersistentData().putBoolean("sit", false);
					if (sourceentity instanceof Player _player && !_player.level().isClientSide())
						_player.displayClientMessage(Component.literal("Minilotl is following"), true);
				}
			} else if (!sourceentity.isShiftKeyDown() && entity.getPersistentData().getBoolean("sit") == false) {
				sourceentity.startRiding(entity);
			}
		} else {
			sourceentity.stopRiding();
		}
	}
}
