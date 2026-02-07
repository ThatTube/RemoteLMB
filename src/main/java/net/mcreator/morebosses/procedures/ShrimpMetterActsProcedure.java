package net.mcreator.morebosses.procedures;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.TickEvent;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;

import net.mcreator.morebosses.init.MorebossesModMobEffects;
import net.mcreator.morebosses.init.MorebossesModEntities;
import net.mcreator.morebosses.init.MorebossesModAttributes;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class ShrimpMetterActsProcedure {
	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			execute(event, event.player.level(), event.player.getX(), event.player.getY(), event.player.getZ(), event.player);
		}
	}

	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		execute(null, world, x, y, z, entity);
	}

	private static void execute(@Nullable Event event, LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		if (entity instanceof LivingEntity _livEnt0 && _livEnt0.hasEffect(MorebossesModMobEffects.SHRIMPED.get())
				&& (entity instanceof LivingEntity _livingEntity1 && _livingEntity1.getAttributes().hasAttribute(MorebossesModAttributes.SHRIMP_METTER.get())
						? _livingEntity1.getAttribute(MorebossesModAttributes.SHRIMP_METTER.get()).getValue()
						: 0) >= 64) {
			if (entity instanceof LivingEntity _livingEntity2 && _livingEntity2.getAttributes().hasAttribute(MorebossesModAttributes.SHRIMP_METTER.get()))
				_livingEntity2.getAttribute(MorebossesModAttributes.SHRIMP_METTER.get()).setBaseValue(0);
			if (world instanceof ServerLevel _level) {
				Entity entityToSpawn = MorebossesModEntities.SHRIMP.get().spawn(_level, BlockPos.containing(x, y, z), MobSpawnType.MOB_SUMMONED);
				if (entityToSpawn != null) {
					entityToSpawn.setYRot(world.getRandom().nextFloat() * 360F);
				}
			}
		}
	}
}
