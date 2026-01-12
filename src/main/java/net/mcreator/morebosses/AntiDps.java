package net.mcreator.morebosses;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;

@Mod.EventBusSubscriber
public class AntiDps {
	// tempo ideal entre hits (20 ticks = 1 segundo)
	private static final int DAMAGE_TIME = 20;

	@SubscribeEvent
	public static void onLivingHurt(LivingHurtEvent event) {
		LivingEntity target = event.getEntity();
		// só funciona em entidades com antidps
		if (!target.getTags().contains("morebosses:antidps"))
			return;
		// entidade que causou o dano (pode ser null: fogo, queda, etc)
		Entity sourceEntity = event.getSource().getEntity();
		// SE quem causou o dano tiver a tag liberada, NÃO reduz
		if (sourceEntity != null && sourceEntity.getTags().contains("morebosses:vaidardpssim")) {
			return;
		}
		long gameTime = target.level().getGameTime();
		long lastHit = target.getPersistentData().getLong("mb_last_hit");
		long delta = gameTime - lastHit;
		int DAMAGE_TIME = 20; // 1 segundo
		double reductionFactor;
		if (delta >= DAMAGE_TIME) {
			reductionFactor = 1.0;
		} else {
			reductionFactor = (double) delta / DAMAGE_TIME;
		}
		// nunca deixa virar zero
		reductionFactor = Math.max(reductionFactor, 0.1);
		event.setAmount((float) (event.getAmount() * reductionFactor));
		target.getPersistentData().putLong("mb_last_hit", gameTime);
	}
}
