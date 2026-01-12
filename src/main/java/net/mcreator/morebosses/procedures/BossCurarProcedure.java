package net.mcreator.morebosses.procedures;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.entity.living.LivingEvent;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.tags.TagKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.Registries;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class BossCurarProcedure {
	@SubscribeEvent
	public static void onEntityTick(LivingEvent.LivingTickEvent event) {
		execute(event, event.getEntity());
	}

	public static void execute(Entity entity) {
		execute(null, entity);
	}

	private static void execute(@Nullable Event event, Entity entity) {
		if (!(entity instanceof LivingEntity living))
			return;
		// se já morreu, some daqui
		if (living.getHealth() <= 0)
			return;
		// só a cada 10 ticks (0.5s)
		if (living.tickCount % 10 != 0)
			return;
		// checa tag de boss
		if (!entity.getType().is(TagKey.create(Registries.ENTITY_TYPE, new ResourceLocation("lmb:boss"))))
			return;
		// se NÃO tiver alvo
		if (entity instanceof Mob mob && mob.getTarget() == null) {
			if (!living.level().isClientSide()) {
				float vidaAtual = living.getHealth();
				float vidaMax = living.getMaxHealth();
				// segurança extra (nunca passa do max)
				if (vidaAtual < vidaMax) {
					living.setHealth(Math.min(vidaAtual + 8.0F, vidaMax));
				}
			}
		}
	}
}
