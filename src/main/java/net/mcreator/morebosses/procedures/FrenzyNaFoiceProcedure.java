package net.mcreator.morebosses.procedures;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffectInstance;

import net.mcreator.morebosses.init.MorebossesModMobEffects;
import net.mcreator.morebosses.init.MorebossesModItems;

@Mod.EventBusSubscriber
public class FrenzyNaFoiceProcedure {
	@SubscribeEvent
	public static void onEntityAttacked(LivingAttackEvent event) {
		Entity source = event.getSource().getEntity();
		if (!(source instanceof LivingEntity attacker))
			return;
		// checa se tá com a foice
		if (attacker.getMainHandItem().getItem() != MorebossesModItems.MACABRE_SCYTHE.get())
			return;
		if (attacker.level().isClientSide())
			return;
		int amplifier = 0;
		if (attacker.hasEffect(MorebossesModMobEffects.FRENZY.get())) {
			amplifier = attacker.getEffect(MorebossesModMobEffects.FRENZY.get()).getAmplifier() + 1;
		}
		// cap no lvl 4
		if (amplifier > 4)
			amplifier = 4;
		attacker.addEffect(new MobEffectInstance(MorebossesModMobEffects.FRENZY.get(), 60, // duração
				amplifier, false, false));
	}
}
