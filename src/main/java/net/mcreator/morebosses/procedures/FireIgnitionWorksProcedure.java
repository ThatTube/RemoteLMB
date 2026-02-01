package net.mcreator.morebosses.procedures;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffectInstance;

import net.mcreator.morebosses.init.MorebossesModMobEffects;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class FireIgnitionWorksProcedure {
	@SubscribeEvent
	public static void onEntityAttacked(LivingAttackEvent event) {
		if (event != null && event.getEntity() != null) {
			execute(event, event.getEntity(), event.getSource().getEntity());
		}
	}

	public static void execute(Entity entity, Entity sourceentity) {
		execute(null, entity, sourceentity);
	}

	private static void execute(@Nullable Event event, Entity entity, Entity sourceentity) {
		if (entity == null || sourceentity == null)
			return;
		if (sourceentity instanceof LivingEntity _livEnt0 && _livEnt0.hasEffect(MorebossesModMobEffects.FLAMMABLE.get()) && entity instanceof LivingEntity _livEnt1 && _livEnt1.hasEffect(MorebossesModMobEffects.FLAMING_IGNITION.get())) {
			entity.setSecondsOnFire(20);
		} else if (!(sourceentity instanceof LivingEntity _livEnt3 && _livEnt3.hasEffect(MorebossesModMobEffects.FLAMMABLE.get())) && entity instanceof LivingEntity _livEnt4 && _livEnt4.hasEffect(MorebossesModMobEffects.FLAMING_IGNITION.get())) {
			if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
				_entity.addEffect(new MobEffectInstance(MorebossesModMobEffects.FLAMMABLE.get(), 60, 0));
		}
	}
}
